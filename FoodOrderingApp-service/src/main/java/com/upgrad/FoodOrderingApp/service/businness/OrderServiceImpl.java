package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderDao orderDao;

    /**
     * The method implements the business logic for getting customer order details by customer uuid.
     */
    @Override
    public List<OrderEntity> getOrdersByCustomers(String customerId) {
       return orderDao.getOrdersByCustomers(customerId);
    }

    /**
     * The method implements the business logic for saving order details endpoint.
     */
    @Override
    public OrderEntity saveOrder(OrderEntity orderEntity) {
        return orderDao.saveOrder(orderEntity);
    }

    /**
     * The method implements the business logic for saving order item entity.
     */
    @Override
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        return orderDao.saveOrderItem(orderItemEntity);
    }

    /**
     * The method implements the business logic for getting coupon details by coupon name endpoint.
     */
    @Override
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
        if (couponName.isEmpty()) throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        else if (orderDao.getCouponByCouponName(couponName)==null)
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        return orderDao.getCouponByCouponName(couponName);
    }

    /**
     * The method implements the business logic for getting coupon details by coupon uuid.
     */
    @Override
    public CouponEntity getCouponByCouponId(String couponId) throws CouponNotFoundException {
        if (orderDao.getCouponByCouponId(couponId)==null)
            throw new CouponNotFoundException("CPF-002","No coupon by this id");
        return orderDao.getCouponByCouponId(couponId);
    }


}
