package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface ReportMapper {

    /**
     * 条件查询用户数量
     */
    Integer countUserByMap(Map map);

    /**
     * map条件查询
     */
    Double sumTurnoverByMap(Map map);

    /**
     * map条件查询
     */
    Integer countOrderByMap(Map map);
}
