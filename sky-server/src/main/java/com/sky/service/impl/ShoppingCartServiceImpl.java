package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;


    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断是否已经存在 通过用ShoppingCart对象动态sql查询
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());// Web 应用中每个请求通常由独立线程处理

        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(shoppingCart);
        if (shoppingCartList != null && !shoppingCartList.isEmpty()) {
            //购物车条目存在
            ShoppingCart cart = shoppingCartList.get(0);
            //更新购物车条目 数量+1
            cart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
            return;
        }
        //不存在，插入一条数据
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        Long dishId = shoppingCartDTO.getDishId();
        if (dishId != null) {
            //菜品 设置菜品相关冗余字段
            Dish dish = dishMapper.getById(dishId);
            shoppingCart.setDishId(dishId);
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setName(dish.getName());
            shoppingCart.setAmount(dish.getPrice());
        } else {
            //套餐 设置套餐相关冗余字段
            Long setmealId = shoppingCartDTO.getSetmealId();
            Setmeal setmeal = setMealMapper.selectById(setmealId);
            shoppingCart.setSetmealId(setmealId);
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setAmount(setmeal.getPrice());
        }
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    /**
     * 查询购物车
     */
    @Override
    public List<ShoppingCart> getShoppingCartList() {
        Long currentId = BaseContext.getCurrentId();
        //查询当前用户的购物车 selectList方法的动态sql复用
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(currentId).build();
        return shoppingCartMapper.selectList(shoppingCart);
    }
}
