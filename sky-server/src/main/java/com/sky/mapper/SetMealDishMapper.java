package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 套餐与菜品的关联表,与套餐本身信息无关
 */
@Mapper
public interface SetMealDishMapper {

    /**
     * 跟据菜品id获取关联套餐的id列表
     */
    List<Long> getSetMealIdsByDishIds(List<Long> ids);

    /**
     * 批量插入套餐菜品关系表
     */
    @AutoFill(OperationType.INSERT)
    void insertSetmealDishes(List<SetmealDish> setmealDishes);

    /**
     * 批量删除套餐菜品关系表
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据套餐id查询套餐菜品关系表
     */
    List<SetmealDish> selectBySetmealId(Long id);
}
