package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 搜索购物车
     *
     * @param shoppingCart 购物车对象
     * @return 购物车条目列表
     */
    List<ShoppingCart> selectList(ShoppingCart shoppingCart);

    /**
     * 插入购物车条目
     * @param shoppingCart
     */
    @Insert("INSERT INTO shopping_cart (user_id, dish_id, setmeal_id, name, amount, number, image, create_time) " +
            "VALUES (#{userId}, #{dishId}, #{setmealId}, #{name}, #{amount}, #{number}, #{image}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 删除购物车条目
     */
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    void deleteByUserId(Long currentId);

    /**
     * 更新购物车条目数量
     */
    @Update("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    void updateNumberById(ShoppingCart cart);
}
