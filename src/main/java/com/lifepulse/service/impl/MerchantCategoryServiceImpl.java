package com.lifepulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifepulse.entity.MerchantCategory;
import com.lifepulse.mapper.MerchantCategoryMapper;
import com.lifepulse.service.MerchantCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 商户分类服务实现
 * 功能：树形分类、父子分类查询、前端菜单渲染
 */
@Service
public class MerchantCategoryServiceImpl
        extends ServiceImpl<MerchantCategoryMapper, MerchantCategory>
        implements MerchantCategoryService {

    private final MerchantCategoryMapper categoryMapper;

    @Autowired
    public MerchantCategoryServiceImpl(MerchantCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 获取类目树形结构（你的原版递归写法）
     */
    @Override
    public List<MerchantCategory> getCategoryTree() {
        // 1. 查询所有启用的类目
        List<MerchantCategory> allList = categoryMapper.selectAllEnabled();

        // 2. 筛选出一级类目（parentId=0）
        List<MerchantCategory> rootList = allList.stream()
                .filter(item -> item.getParentId() == 0)
                .collect(Collectors.toList());

        // 3. 递归设置子类目
        for (MerchantCategory root : rootList) {
            root.setChildren(getChildren(root.getId(), allList));
        }
        return rootList;
    }

    /**
     * 递归获取子类目（你的原版逻辑）
     */
    private List<MerchantCategory> getChildren(Long parentId, List<MerchantCategory> allList) {
        List<MerchantCategory> children = new ArrayList<>();
        for (MerchantCategory item : allList) {
            if (parentId.equals(item.getParentId())) {
                children.add(item);
                // 递归
                item.setChildren(getChildren(item.getId(), allList));
            }
        }
        return children;
    }

    /**
     * 根据父ID查询
     */
    @Override
    public List<MerchantCategory> getByParentId(Long parentId) {
        LambdaQueryWrapper<MerchantCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantCategory::getParentId, parentId);
        wrapper.eq(MerchantCategory::getStatus, 1);
        wrapper.orderByAsc(MerchantCategory::getSort);
        return list(wrapper);
    }
}