package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api("商店模块")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

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
        log.info("用户获取营业状态:{}", status == 1 ? "营业中" : "打烊");
        return Result.success(status);
    }
}
