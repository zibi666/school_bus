package com.lm.school_bus.mapper;

import com.lm.school_bus.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderMapper {
    List<Order> selectByStatusOrderByCreateTimeDesc(String status);
    List<Order> selectAllOrderByCreateTimeDesc();
    Order selectById(Integer orderId);
    int insert(Order order);
    int update(Order order);
    int deleteById(Integer orderId);
    List<Order> selectByStudentIdOrderByCreateTimeDesc(String studentId);
    List<Order> selectByBusId(Integer busId);
}
