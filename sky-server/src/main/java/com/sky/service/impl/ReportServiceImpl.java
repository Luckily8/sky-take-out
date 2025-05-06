package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ReportMapper reportMapper;

    /**
     * 时间段内每日营业额统计
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //获取日期列表
        StringBuilder dateList = new StringBuilder();
        //获取营业额列表
        StringBuilder turnoverList = new StringBuilder();
        //循环遍历日期 条件查询 map
        Map map = new HashMap();

        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            //获取日期
            dateList.append(date).append(",");
            //设置日期开始结束时间为0点和23点59分59秒
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.atTime(23, 59, 59);
            //条件查询: startTime <= create_time <= endTime , status = 5(已完成)
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = reportMapper.sumTurnoverByMap(map);
            turnover = (turnover == null ? 0.0 : turnover); //如果没有数据,则设置为0
            turnoverList.append(turnover).append(",");
            //清空map
            map.clear();
        }
        //去除最后一个逗号
        dateList.deleteCharAt(dateList.length() - 1);
        turnoverList.deleteCharAt(turnoverList.length() - 1);

        //封装数据
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(String.valueOf(dateList))
                .turnoverList(String.valueOf(turnoverList)).build();
        return turnoverReportVO;
    }

    /**
     * 统计用户总数量 当日新增用户数量
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //获取日期列表
        StringBuilder dateList = new StringBuilder();
        //获取当日总用户列表
        StringBuilder totalUserList = new StringBuilder();
        //获取当日新增用户列表
        StringBuilder newUserList = new StringBuilder();
        //循环遍历日期 条件查询 map
        Map map = new HashMap();

        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            //获取日期
            dateList.append(date).append(",");
            //设置日期开始结束时间为0点和23点59分59秒
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.atTime(23, 59, 59);
            //条件查询: startTime <= create_time <= endTime ,
            //只含结束时间时,查询当日总用户
            map.put("endTime", endTime);
            Integer totalCount = reportMapper.countUserByMap(map);
            totalCount = (totalCount == null ? 0 : totalCount); //如果没有数据,则设置为0
            totalUserList.append(totalCount).append(",");
            //包含开始时间时,查询当日新增用户
            map.put("startTime", startTime);
            Integer newCount = reportMapper.countUserByMap(map);
            newCount = (newCount == null ? 0 : newCount);
            newUserList.append(newCount).append(",");
            //清空map
            map.clear();
        }

        //去除最后一个逗号
        dateList.deleteCharAt(dateList.length() - 1);
        totalUserList.deleteCharAt(totalUserList.length() - 1);
        newUserList.deleteCharAt(newUserList.length() - 1);

        //封装数据
        return UserReportVO.builder()
                .dateList(dateList.toString())
                .totalUserList(totalUserList.toString())
                .newUserList(newUserList.toString()).build();
    }

    /**
     * 统计订单数量 计算订单完成率
     */
//    @Override
//    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
//
//
//    }
}
