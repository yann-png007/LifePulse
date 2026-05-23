package com.lifepulse.controller.admin;

import com.lifepulse.common.Result;
import com.lifepulse.entity.MerchantCategory;
import com.lifepulse.service.MerchantCategoryService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.extension.toolkit.Db.updateById;

@RestController
@RequestMapping("/merchant/category")
public class MerchantCategoryAdminController {

    private final MerchantCategoryService merchantCategoryService;

    public MerchantCategoryAdminController(MerchantCategoryService merchantCategoryService) {
        this.merchantCategoryService = merchantCategoryService;
    }

    @GetMapping("/tree")
    public Result<List<MerchantCategory>> getCategoryTree() {
        List<MerchantCategory> treeList = merchantCategoryService.getCategoryTree();
        return Result.success(treeList);
    }

    /**
     * 根据父ID获取子分类
     */
    @GetMapping("/parent/{parentId}")
    public Result<List<MerchantCategory>> getByParentId(@PathVariable Long parentId) {
        List<MerchantCategory> list = merchantCategoryService.getByParentId(parentId);
        return Result.success(list);
    }

    /**
     * 新增分类
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody MerchantCategory category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Result<String> result = Result.success(category.getId().toString());
        result.setMsg("添加成功");
        return result;
    }

    /**
     * 修改分类
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody MerchantCategory category) {
        category.setUpdateTime(LocalDateTime.now());
        updateById(category);
        return Result.success("修改成功");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        merchantCategoryService.removeById(id);
        return Result.success("删除成功");
    }
}