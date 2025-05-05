package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api("购物车相关接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车参数:{}", shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success("添加购物车成功");
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result cleanShoppingCart() {
        log.info("清空购物车");
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    /**
     * 查询购物车
     */
    @GetMapping("/list")
    @ApiOperation("查询购物车")
    public Result<List<ShoppingCart>> showShoppingCart() {
        log.info("查询购物车,用户id:{}", BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartList();
        return Result.success(shoppingCartList);
    }

    /**
     * 减少购物车中的一个商品
     */
    @PostMapping("/sub")
    @ApiOperation("减少购物车中的一个商品")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("减少购物车中的一个商品参数:{}", shoppingCartDTO);
        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success("减少购物车中的一个商品成功");
    }



}
