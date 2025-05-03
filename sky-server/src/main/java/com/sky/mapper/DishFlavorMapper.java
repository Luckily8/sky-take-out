package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入菜品口味(不需要记录操作)
     * 并且参数不是自定义的实体类,没有set方法
     */
//    @AutoFill(OperationType.INSERT)
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 批量删除菜品口味
     */
    void deleteByDishIds(List<Long> ids);


    /**
     * 根据菜品id查询口味
     */
    List<DishFlavor> getFlavorByDishId(Long id);
}
