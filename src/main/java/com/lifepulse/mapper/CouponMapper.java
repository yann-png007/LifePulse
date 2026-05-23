package com.lifepulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifepulse.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 原子扣减库存（配合Lua脚本实现防超卖）
     */
    @Update("UPDATE lp_coupon SET used_stock = used_stock + 1 WHERE id = #{couponId} AND used_stock < total_stock")
    int deductStock(@Param("couponId") Long couponId);
}
