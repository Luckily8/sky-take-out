package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId") // 清除单个套餐缓存
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐，参数：{}", setmealDTO);
        setMealService.insert(setmealDTO);
        return Result.success();
    }

    /**
     * 根据setmealIDs批量删除套餐
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true) // 清除所有套餐缓存
    public Result delete(@RequestParam("ids") List<Long> ids) {
        log.info("批量删除套餐，参数：{}", ids);
        setMealService.delete(ids);
        return Result.success();
    }

    /**
     * 单个起售/停售套餐
     */
    @PostMapping("/status/{status}")
    @ApiOperation("单个起售/停售套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true) // 清除所有套餐缓存
    public Result updateStatus(@PathVariable("status") Integer status, Long id) {
        log.info("单个起售/停售套餐，参数：status = {}, id = {}", status, id);
        setMealService.updateStatus(status, id);
        return Result.success();
    }

    /**
     * 回显：根据id查询套餐
     */
    @GetMapping("/{id}")
    @ApiOperation("回显：根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable("id") Long id) {
        log.info("回显：根据id查询套餐，参数：id = {}", id);
        SetmealVO setmealVO = setMealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     */
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true) // 清除所有套餐缓存
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐，参数：{}", setmealDTO);
        setMealService.update(setmealDTO);
        return Result.success();
    }



}
