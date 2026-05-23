package com.lifepulse.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifepulse.entity.Merchant;
import com.lifepulse.mapper.MerchantMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MerchantColdTask {

    private final MerchantMapper merchantMapper;

    public MerchantColdTask(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    // 每日凌晨2点执行，把30天未更新的热数据转为冷数据
    @Scheduled(cron = "0 0 2 * * ?")
    public void moveColdData() {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(30);

        // 1. 查询符合条件的热数据
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getHotFlag, 1)
                .lt(Merchant::getUpdateTime, expireTime)
                .eq(Merchant::getIsDeleted, 0);
        List<Merchant> hotList = merchantMapper.selectList(wrapper);

        if (hotList.isEmpty()) {
            return;
        }

        // 2. 提取ID列表
        List<Long> idList = hotList.stream()
                .map(Merchant::getId)
                .collect(Collectors.toList());

        // 3. 批量更新为冷数据
        merchantMapper.batchUpdateToCold(idList);
    }
}