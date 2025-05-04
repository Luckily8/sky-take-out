package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api("菜品操作相关接口")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品及口味
     */
    @PostMapping
    @ApiOperation("新增菜品及口味")
    public Result addDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品及口味，参数：{}", dishDTO);
        dishService.addDishWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询，参数：page = {}, pageSize = {} ", dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        PageResult result = dishService.page(dishPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    @ApiOperation("批量删除")
    public Result deleteDish(@RequestParam List<Long> ids ) {
        log.info("批量删除菜品，参数：{}", ids);
        dishService.delete(ids);
        return Result.success();
    }

    /**
     * 回显:跟据id查询菜品及口味
     */
    @GetMapping("/{id}")
    @ApiOperation("回显:跟据id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("回显:跟据id查询菜品，参数：{}", id);
        DishVO dishVO = dishService.getDishById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品信息以及口味信息
     */
    @PutMapping
    @ApiOperation("修改菜品信息以及口味信息")
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息以及口味信息，参数：{}", dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 单个菜品起售/停售
     * 同时包含该菜品的套餐也会随之停售
     */
    @PostMapping("/status/{status}")
    @ApiOperation("单个菜品起售/停售")
    public Result updateDishStatus(@PathVariable Integer status, @RequestParam Long id) {
        log.info("单个菜品起售/停售，参数：status = {}, id = {}", status, id);
        dishService.updateDishStatus(status, id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品列表")
    public Result<List<Dish>> list(@RequestParam Long categoryId) {
        log.info("根据分类id查询菜品列表，参数：categoryId = {}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }


}
