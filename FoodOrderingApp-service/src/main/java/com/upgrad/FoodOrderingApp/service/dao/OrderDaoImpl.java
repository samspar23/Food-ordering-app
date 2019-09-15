package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Order;
import java.util.List;

/**
 * OrderDao class provides the database access for all the endpoints in Order controller.
 */
@Repository
@Transactional
public class OrderDaoImpl implements OrderDao{

    //When a container of the application(be it a Java EE container or any other custom container like Spring) manages the lifecycle of the Entity Manager, the Entity Manager is said to be Container Managed. The most common way of acquiring a Container Managed EntityManager is to use @PersistenceContext annotation on an EntityManager attribute.
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrderEntity> getOrdersByCustomers(String customerId) {
        try {
            return entityManager.createNamedQuery("ordersByCustomer", OrderEntity.class).setParameter("customerId", customerId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public OrderEntity saveOrder(OrderEntity orderEntity) {
        entityManager.persist(orderEntity);
        return orderEntity;
    }

    @Override
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        entityManager.persist(orderItemEntity);
        return orderItemEntity;
    }

    @Override
    public CouponEntity getCouponByCouponName(String couponName) {
        try {
            return entityManager.createNamedQuery("couponByName", CouponEntity.class).setParameter("couponName", couponName).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public CouponEntity getCouponByCouponId(String couponId) {
        try {
            return entityManager.createNamedQuery("couponByUUID", CouponEntity.class).setParameter("couponId", couponId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public List<OrderEntity> getAllOrdersByAddressUUID(String uuid){
        try {
            return entityManager.createNamedQuery("getAllOrdersByAddressUUID", OrderEntity.class).setParameter("uuid", uuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
