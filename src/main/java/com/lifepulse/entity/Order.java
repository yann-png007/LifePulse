package com.lifepulse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("lp_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;        // 用户ID
    private Long couponId;      // 关联优惠券ID
    private Long merchantId;    // 关联商户ID
    private Integer status;     // 订单状态 0待支付 1已支付 2已取消 3已完成
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime expireTime; // 超时时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", couponId=" + couponId +
                ", merchantId=" + merchantId +
                ", status=" + status +
                ", createTime=" + createTime +
                ", payTime=" + payTime +
                ", expireTime=" + expireTime +
                '}';
    }
}