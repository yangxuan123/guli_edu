package com.haha.guli.trade.service;

import com.haha.guli.trade.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author yang
 * @since 2021-08-13
 */
public interface OrderService extends IService<Order> {

    String saveOrder(String courseId, String id);

    Order getByOrderId(String orderId, String memberId);

    Boolean isBuyByCourseId(String courseId, String id);

    List<Order> selectByMemberId(String id);

    boolean removeById(String orderId, String memberId);

    Order getOrderByOrderNo(String orderNo);

    void updateOrderStatus(Map<String, String> notifyMap);

    boolean queryPayStatus(String orderNo);
}
