package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 定时清理超时订单
     */
    @Scheduled(cron = "0 * * * * *")
    public void processTimeoutOrder() {
        log.info("开始清理超时订单...");
        // 调用 mapper 方法清理超时未支付订单(距离下单时间超过15分钟的订单)
        LocalDateTime ddl = LocalDateTime.now().plusMinutes(-15);
        List<Orders> ordersList = orderMapper.getByStatusAndDdl(Orders.PENDING_PAYMENT, ddl);
        // 订单列表不为空,则进行清理
        int count = ordersList.size();
        if (count > 0) {
            ordersList.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时未支付,自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }
        log.info("清理超时订单成功,共清理了{}条数据", count);
    }

    /**
     * 定时清理已派送订单
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void processDeliveredOrder() {
        log.info("开始清理已派送订单...");
        // 调用 mapper 方法清理已派送订单(距离派送时间超过1小时的订单)
        LocalDateTime ddl = LocalDateTime.now().plusHours(-1);
        List<Orders> ordersList = orderMapper.getByStatusAndDdl(Orders.DELIVERY_IN_PROGRESS, ddl);
        // 订单列表不为空,则进行清理
        int count = ordersList.size();
        if (count > 0) {
            ordersList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            });
        }
        log.info("更新已派送订单成功,共处理了{}条数据", count);
    }
}
