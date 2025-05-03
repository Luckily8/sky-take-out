package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
 public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDishWithFlavor(DishDTO dishDTO) {
        //获取菜品对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //保存菜品
        dishMapper.insert(dish);
        //获取菜品id
        Long dishId = dish.getId();
        //获取口味列表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //遍历口味列表,赋值id
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            //保存口味列表
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //获取分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //查询
        Page<DishVO> page = dishMapper.selectPage(dishPageQueryDTO);
        //获取分页数据
        return new PageResult(page.getTotal(), page.getResult());
    }
}
