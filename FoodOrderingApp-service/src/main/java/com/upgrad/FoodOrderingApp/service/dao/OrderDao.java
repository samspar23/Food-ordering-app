package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;

import java.util.List;

/*
 * This OrderDao interface gives the list of all the dao methods that exist in the order dao implementation class.
 * Service class will be calling the dao methods by this interface.
 */
public interface OrderDao {

    List<OrderEntity> getOrdersByCustomers(String customerId);
    OrderEntity saveOrder(OrderEntity orderEntity);
    OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity);
    CouponEntity getCouponByCouponName(String couponName);
    CouponEntity getCouponByCouponId(String couponId);
    List<OrderEntity> getAllOrdersByAddressUUID(String uuid);
}
