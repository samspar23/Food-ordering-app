package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * PaymentDao class provides the database access for all the endpoints in Payment controller.
 */
@Repository
public class PaymentDaoImpl implements PaymentDao{

    //When a container of the application(be it a Java EE container or any other custom container like Spring) manages the lifecycle of the Entity Manager, the Entity Manager is said to be Container Managed. The most common way of acquiring a Container Managed EntityManager is to use @PersistenceContext annotation on an EntityManager attribute.
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PaymentEntity> getAllPaymentMethods() {
        try {
            return entityManager.createNamedQuery("getAllPaymentMethods", PaymentEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public PaymentEntity getPaymentByUUID(String paymentId) {
        try {
            return entityManager.createNamedQuery("getPaymentByUUID", PaymentEntity.class).setParameter("paymentId", paymentId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
