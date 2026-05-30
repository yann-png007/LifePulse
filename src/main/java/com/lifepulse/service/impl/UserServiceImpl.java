package com.lifepulse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifepulse.common.PageResult;
import com.lifepulse.common.ResultCode;
import com.lifepulse.constant.CacheConstant;
import com.lifepulse.constant.UserConstant;
import com.lifepulse.dto.LoginByCodeDTO;
import com.lifepulse.dto.LoginByPasswordDTO;
import com.lifepulse.entity.User;
import com.lifepulse.exception.BizException;
import com.lifepulse.mapper.UserMapper;
import com.lifepulse.service.BloomFilterInitService;
import com.lifepulse.service.UserService;
import com.lifepulse.util.JwtUtil;
import com.lifepulse.util.PasswordUtil;
import com.lifepulse.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    public UserServiceImpl(UserMapper userMapper, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate, RedissonClient redissonClient) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redissonClient = redissonClient;
    }

    @Override
    public void sendLoginCode(String phone) {
        String code = RandomUtil.generateNumeric(6);
        String redisKey = CacheConstant.LOGIN_CODE_PREFIX + phone;
        stringRedisTemplate.opsForValue().set(redisKey, code, CacheConstant.LOGIN_CODE_EXPIRE);
        log.info("已为手机号 {} 生成登录验证码: {}，有效期 {} 分钟。", phone, code, CacheConstant.LOGIN_CODE_EXPIRE.toMinutes());
        // 在此集成真实的短信服务提供商API
    }

    @Override
    public String loginByPassword(LoginByPasswordDTO loginDTO) {
        String phone = loginDTO.getPhone();
        String password = loginDTO.getPassword();

        // 1. 布隆过滤器前置判断
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BloomFilterInitService.USER_PHONE_BLOOM_FILTER);
        if (!bloomFilter.contains(phone)) {
            log.warn("布隆过滤器拦截：尝试使用不存在的手机号 {} 进行密码登录", phone);
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }

        // 2. 查询用户并验证密码
        User user = userMapper.selectUserByPhone(phone);
        if (user == null) {
            // 布隆过滤器存在误判，数据库二次确认
            log.error("布隆过滤器误判：手机号 {} 不存在于数据库，但通过了布隆过滤器", phone);
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BizException(ResultCode.USER_ACCOUNT_DISABLE);
        }

        // 4. 生成Token
        return jwtUtil.createToken(user.getId(), user.getRole());
    }

    @Override
    @Transactional
    public String loginByCode(LoginByCodeDTO loginDTO) {
        String phone = loginDTO.getPhone();
        String code = loginDTO.getCode();

        // 1. 校验验证码
        String redisKey = CacheConstant.LOGIN_CODE_PREFIX + phone;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (!code.equals(storedCode)) {
            throw new BizException(ResultCode.VERIFY_CODE_ERROR);
        }

        // 2. 验证码正确，删除验证码，防止重复使用
        stringRedisTemplate.delete(redisKey);

        // 3. 查询用户，如果不存在则自动注册
        User user = userMapper.selectUserByPhone(phone);
        if (user == null) {
            log.info("手机号 {} 未注册，将执行自动注册流程。", phone);
            user = createUserWithPhone(phone);
        }

        // 4. 生成Token
        return jwtUtil.createToken(user.getId(), user.getRole());
    }

    /**
     * 为新手机号创建用户（自动注册）
     * @param phone 手机号
     * @return 创建好的用户实体
     */
    private User createUserWithPhone(String phone) {
        // 使用分布式锁防止并发场景下同一手机号重复注册
        RLock lock = redissonClient.getLock(CacheConstant.REGISTER_LOCK_PREFIX + phone);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    // 双重检查，再次确认用户是否已存在
                    User existingUser = userMapper.selectUserByPhone(phone);
                    if (existingUser != null) {
                        log.warn("获取锁后发现用户已存在，手机号: {}", phone);
                        return existingUser;
                    }

                    User user = new User();
                    user.setPhone(phone);
                    // 生成随机默认用户名和密码
                    user.setUsername(UserConstant.DEFAULT_USERNAME_PREFIX + RandomUtil.generateAlphabetic(8));
                    user.setPassword(PasswordUtil.encrypt(UserConstant.DEFAULT_PASSWORD));
                    user.setRole(UserConstant.DEFAULT_ROLE);
                    user.setStatus(1);
                    user.setCreateTime(LocalDateTime.now());
                    user.setUpdateTime(LocalDateTime.now());

                    userMapper.insertUser(user);
                    log.info("新用户创建成功，手机号: {}, 用户ID: {}", phone, user.getId());

                    // 将新手机号添加到布隆过滤器
                    RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BloomFilterInitService.USER_PHONE_BLOOM_FILTER);
                    bloomFilter.add(phone);
                    log.info("新用户手机号 {} 已添加到布隆过滤器。", phone);

                    return user;
                } finally {
                    lock.unlock();
                }
            } else {
                log.error("获取注册锁失败，手机号: {}", phone);
                throw new BizException(ResultCode.FAIL, "系统繁忙，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取注册锁时被中断，手机号: {}", phone, e);
            throw new BizException(ResultCode.FAIL, "系统异常，请稍后重试");
        }
    }



    @Override
    @Transactional
    public void addUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        if(user.getStatus() == null){
            user.setStatus(1);
        }
        userMapper.insertUser(user);

        // 将新用户手机号添加到布隆过滤器
        if (StringUtils.hasText(user.getPhone())) {
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BloomFilterInitService.USER_PHONE_BLOOM_FILTER);
            bloomFilter.add(user.getPhone());
            log.info("新用户手机号 {} 已添加到布隆过滤器。", user.getPhone());
        }
    }

    // 分页查询用户
    @Override
    public PageResult<User> getUserPage(Integer pageNum,Integer pageSize){
        PageResult<User> page = new PageResult<>();
        Integer start = (pageNum-1)*pageSize;
        List<User> list = userMapper.selectUserPage(start,pageSize);
        Long total = userMapper.selectUserCount();
        page.setList(list);
        page.setTotal(total);
        return page;
    }

    // 修改密码
    @Override
    @Transactional
    public void modifyPwd(Long userId,String oldPwd,String newPwd){
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_EXIST);
        }
        if(!PasswordUtil.verify(oldPwd,user.getPassword())){
            throw new BizException(ResultCode.OLD_PASSWORD_ERROR);
        }
        String newEncode = PasswordUtil.encrypt(newPwd);
        userMapper.updatePwd(userId,newEncode);
    }

    // 退出登录
    @Override
    public void logout(String token){
        stringRedisTemplate.delete("login:token:"+token);
    }

    @Override
    public User getUserById(Long id){
        return userMapper.selectUserById(id);
    }

    @Override
    @Transactional
    public void editUser(User user){
        userMapper.updateUser(user);
    }

    @Override
    @Transactional
    public void removeUser(Long id){
        userMapper.deleteUser(id);
    }
}