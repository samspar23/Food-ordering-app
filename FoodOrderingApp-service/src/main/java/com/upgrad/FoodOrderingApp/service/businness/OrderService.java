package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;

import java.util.List;

/*
 * This OrderService interface gives the list of all the service that exist in the order service implementation class.
 * Controller class will be calling the service methods by this interface.
 */
public interface OrderService {

    List<OrderEntity> getOrdersByCustomers(String customerId);
    OrderEntity saveOrder(OrderEntity orderEntity);
    OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity);
    CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException;
    CouponEntity getCouponByCouponId(String couponId) throws CouponNotFoundException;
}
