package com.lifepulse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifepulse.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    // 根据类目ID查询商户列表
    @Select("SELECT * FROM lp_merchant WHERE category_id = #{categoryId} AND status = 1")
    List<Merchant> selectByCategoryId(Long categoryId);

    // 分页查热数据商户
    List<Merchant> selectHotPage(@Param("offset") Integer offset, @Param("size") Integer size);
    // 分页查冷数据商户
    List<Merchant> selectColdPage(@Param("offset") Integer offset, @Param("size") Integer size);
    // 批量更新为冷数据
    int batchUpdateToCold(@Param("idList") List<Long> idList);
}