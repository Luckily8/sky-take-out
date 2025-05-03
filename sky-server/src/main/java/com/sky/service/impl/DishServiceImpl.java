package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
 public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SetMealMapper setMealMapper;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 是否在售(只能多条sql语句查询)
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 是否被套餐关联 使用动态sql in查询关联的套餐ids
        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(ids);
        if(setMealIds != null && !setMealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 删除菜品
        dishMapper.deleteByIds(ids);
        // 删除关联的口味表
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品及口味
     */
    @Override
    public DishVO getDishById(Long id) {
        //获取菜品及口味信息以及分类名称
        Dish dish = dishMapper.getById(id); //包含分类id
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getFlavorByDishId(id);
        //封装VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavorList);
//        dishVO.setCategoryName(categoryMapper.getNameById(dish.getCategoryId())); //跟据分类id查找分类名称,可以省略,因为前端自动跟据类型查询了

        return dishVO;
    }

    /**
     * 修改菜品信息以及口味信息
     */
    @Override
    @Transactional
    public void updateDishWithFlavor(DishDTO dishDTO) {
        //修改菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //修改菜品对应的口味表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //删除原有口味
            dishFlavorMapper.deleteByDishIds(Collections.singletonList(dish.getId()));
            //遍历口味列表,赋值id
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            //保存口味列表
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 单个菜品起售 1/停售 0
     */
    @Override
    public void updateDishStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status).build();
        dishMapper.update(dish);

        //如果是停售,则停售套餐
        if (Objects.equals(status, StatusConstant.DISABLE)) {
            List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(Collections.singletonList(dish.getId())); //查找外键关系时使用含dish的表
            if (setMealIds != null && !setMealIds.isEmpty()) { //确实关联了套餐
                for (Long setMealId : setMealIds) { //停售所有关联的套餐
                    Setmeal setmeal = Setmeal.builder()
                            .id(setMealId)
                            .status(StatusConstant.DISABLE).build();
                    setMealMapper.update(setmeal); //停售套餐,修改套餐时使用不包含dish的表
                }
            }
        }
    }

}
