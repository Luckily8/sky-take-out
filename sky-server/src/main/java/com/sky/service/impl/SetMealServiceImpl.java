package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 套餐分页查询
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开启分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        //查询数据
        Page<SetmealVO> pages = setMealMapper.selectPage(setmealPageQueryDTO);
        //封装数据
        return new PageResult(pages.getTotal(), pages.getResult());
    }

    /**
     * 新增套餐
     * 同时插入套餐和套餐菜品关系表
     */
    @Override
    @Transactional
    public void insert(SetmealDTO setmealDTO) {
        //插入套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.insert(setmeal);
        //主键返回
        //获取生成的套餐id
        Long setmealId = setmeal.getId();
        //插入套餐菜品关系表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId)); //DTO中不包含setmealId
            //批量插入
            setMealDishMapper.insertSetmealDishes(setmealDishes);
        }
    }

    /**
     * 批量删除套餐
     * 1.在售套餐不允许删除
     * 2.删除套餐菜品关系表
     */
    @Override
    @Transactional
    public void delete(List<Long> setmealIds) {
        //根据套餐id查询套餐在售情况
        setmealIds.forEach(id -> {
           Setmeal setmeal =  setMealMapper.selectById(id);
           if (Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)) { //套餐在售
               throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
           }
        });
        //根据setmealid删除套餐菜品关系表
        setMealDishMapper.deleteBySetmealIds(setmealIds);
        //根据setmealid删除套餐
        setMealMapper.deleteByIds(setmealIds);
    }

    /**
     * 单个起售/停售套餐
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        //直接更新整个套餐，由动态sql来判断
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status).build();
        setMealMapper.update(setmeal);
    }
}
