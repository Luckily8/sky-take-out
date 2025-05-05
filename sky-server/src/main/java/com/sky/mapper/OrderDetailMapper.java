package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 插入多条订单详情
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单明细
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
