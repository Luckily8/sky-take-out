package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
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

    /**
     * 条件查询: startTime <= create_time <= endTime , status = 5(已完成) 分页 0,10
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime startTime, LocalDateTime endTime, Integer status);
}
