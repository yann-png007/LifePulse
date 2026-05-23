package com.lifepulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifepulse.entity.MerchantCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface MerchantCategoryMapper extends BaseMapper<MerchantCategory> {

    // 查询所有启用的类目
    @Select("SELECT * FROM lp_merchant_category WHERE status = 1 ORDER BY sort")
    List<MerchantCategory> selectAllEnabled();
}