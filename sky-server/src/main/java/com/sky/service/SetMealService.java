package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void insert(SetmealDTO setmealDTO);

    void delete(List<Long> ids);

    void updateStatus(Integer status, Long id);

    SetmealVO getById(Long id);

    void update(SetmealDTO setmealDTO);
}
