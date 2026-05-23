package com.lifepulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifepulse.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CouponStockMapper extends BaseMapper<Coupon> {
    /**
     * 原子扣减库存
     * 直接减少total_stock，校验库存>0、状态正常、在有效期
     */
    @Update("UPDATE lp_coupon " +
            "SET total_stock = total_stock - 1 " +
            "WHERE id = #{couponId} " +
            "AND total_stock > 0 " +
            "AND status = 1 " +
            "AND start_time < NOW() " +
            "AND end_time > NOW()")
    int deductCouponStock(@Param("couponId") Long couponId);
}