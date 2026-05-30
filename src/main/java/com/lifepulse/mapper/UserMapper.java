package com.lifepulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifepulse.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {



    @Select("select * from lp_user where phone=#{phone}")
    User selectUserByPhone(String phone);
    @Select("select * from lp_user where id=#{id}")
    User selectUserById(Long id);

    @Insert("insert into lp_user(username,password,real_name,phone,gender,status) values(#{username},#{password},#{realName},#{phone},#{gender},#{status})")
    int insertUser(User user);
    @Update("update lp_user set username=#{username},password=#{password},real_name=#{realName},phone=#{phone},gender=#{gender},status=#{status} where id=#{id}")
    int updateUser(User user);
    @Delete("delete from lp_user where id=#{id}")
    int deleteUser(Long id);

    // 分页查询
    @Select("select * from lp_user limit #{start},#{pageSize}")
    List<User> selectUserPage(@Param("start") Integer start,@Param("pageSize") Integer pageSize);

    // 统计总数
    @Select("select count(*) from lp_user")
    Long selectUserCount();
    // 修改密码
    @Update("update lp_user set password=#{newPwd} where id=#{userId}")
    int updatePwd(@Param("userId")Long userId,@Param("newPwd")String newPwd);

    /**
     * 查询所有用户的手机号（用于初始化布隆过滤器）
     * @return 手机号列表
     */
    @Select("select phone from lp_user where phone is not null and phone != ''")
    List<String> selectAllPhones();
}