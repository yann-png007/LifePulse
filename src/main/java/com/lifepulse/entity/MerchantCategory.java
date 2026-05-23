package com.lifepulse.entity;
import java.time.LocalDateTime;
import java.util.List;

public class MerchantCategory {
    private Long id;
    private Long parentId;
    private String name;
    private String icon;
    private Integer level;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<MerchantCategory> children; // 子类目列表，用于构建树形结构

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getParentId() {return parentId;}

    public void setParentId(Long parentId) {this.parentId = parentId;}

    public String getName() { return name;}

    public void setName(String name) {this.name = name;}

    public Integer getLevel() { return level; }

    public void setLevel(Integer level) {this.level = level;}

    public Integer getSort() {return sort;}

    public void setSort(Integer sort) {this.sort = sort;}

    public Integer getStatus() {return status;}

    public void setStatus(Integer status) {this.status = status;}

    public List<MerchantCategory> getChildren() {return children;}

    public void setChildren(List<MerchantCategory> children) {this.children = children;}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}