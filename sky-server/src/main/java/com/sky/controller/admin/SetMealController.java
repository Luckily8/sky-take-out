package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api("套餐操作相关接口")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询，参数：page = {}, pageSize = {} ", setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        PageResult result = setMealService.page(setmealPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 新增套餐
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐，参数：{}", setmealDTO);
        setMealService.insert(setmealDTO);
        return Result.success();
    }



}
