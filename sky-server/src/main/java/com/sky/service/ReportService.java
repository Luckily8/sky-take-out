package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 时间段内每日营业额统计
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计用户数量
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计订单数量
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计商品销量前10
     */
    SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end);
}
