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
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 新增菜品
     */
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
     * @return PageResult(total, VO对象列表)
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //获取分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //查询 从DTO解析出查询条件,并new为dish对象,包装mapper方法在非分页查询清空下的复用
        //因为 PageHelper 的分页设置是基于线程上下文的，当前线程中开启的分页配置会影响后续的查询操作，直到分页设置被清除或线程结束
        Dish dish = Dish.builder()
                .status(dishPageQueryDTO.getStatus())
                .name(dishPageQueryDTO.getName())
                .categoryId(dishPageQueryDTO.getCategoryId())
                .build();
        Page<DishVO> page = dishMapper.selectPage(dish);
        //获取分页数据
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     */
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
        Dish dish = dishMapper.getById(id); //返回值包含分类id
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

    /**
     * 根据分类id查询菜品列表
     */
    @Override
    public List<Dish> list(Long categoryId) {
        //查询菜品列表
        List<Dish> list = dishMapper.selectByCategoryId(categoryId);
        //设置分类名称
        return list;
    }

    /**
     * 根据条件查询菜品列表
     * @return List<DishVO>
     */
    @Override
    public List<DishVO> getDishList(Dish dish) {
        //查询菜品列表 并未在该方法开启分页 所以默认返回所有数据
        //复用了mapper的分页查询方法
        List<DishVO> DishVOs = dishMapper.selectPage(dish).getResult(); //getResult去除分页信息
        return DishVOs;
    }

}
