package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 条件查询:根据分类id查询在售菜品
     * 使用Redis缓存
     * @return List<DishVO>
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询在售菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        //跟据分类id动态创建Redis缓存key
        String redisKey = "dish_" + categoryId;

        //查询Redis缓存
        List<DishVO> dishVOList = (List<DishVO>)redisTemplate.opsForValue().get(redisKey);
        if (dishVOList != null && !dishVOList.isEmpty()) {
            //如果Redis缓存中有数据，直接返回
            return Result.success(dishVOList);
        }

        //Redis不存在数据,Mysql查询起售中的菜品
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);

        List<DishVO> list = dishService.getDishList(dish);
        //将查询结果存入Redis缓存
        redisTemplate.opsForValue().set(redisKey, list);

        return Result.success(list);
    }

}
