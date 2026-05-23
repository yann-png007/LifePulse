package com.lifepulse.common;

import lombok.Data;

import java.util.List;
@Data
public class PageResult<T> {
    private Long total;
    private Long pages;
    private List<T> list;
}