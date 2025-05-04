package com.sky.controller.admin;

import com.google.j2objc.annotations.AutoreleasePool;
import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api("商店模块")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置营业状态
     */
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置营业状态为:{}", status == 1 ? "营业中" : "打烊");
        //将营业状态存入Redis
        redisTemplate.opsForValue().set(StatusConstant.SHOP_STATUS_KEY, status);
        return Result.success();
    }

    /**
     * 获取营业状态
     */
    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result getStatus() {
        //从Redis中获取营业状态
        Integer status = (Integer) redisTemplate.opsForValue().get(StatusConstant.SHOP_STATUS_KEY);
        if (status == null) {
            return Result.error("商家未设置营业状态");
        }
        log.info("员工获取营业状态:{}", status == 1 ? "营业中" : "打烊");
        return Result.success(status);
    }
}
