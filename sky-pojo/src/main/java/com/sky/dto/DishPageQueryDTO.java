package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DishPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;

    //分类id 修改为Long类型
    private Long categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;

}
