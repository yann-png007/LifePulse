package com.lifepulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifepulse.entity.MerchantCategory;

import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface MerchantCategoryService extends IService<MerchantCategory> {


    /**
     * 获取类目树形结构
     *
     * @return 树形结构列表
     */
    public List<MerchantCategory> getCategoryTree();

    /**
     * 根据父ID查询子分类
     */
    List<MerchantCategory> getByParentId(Long parentId);

}