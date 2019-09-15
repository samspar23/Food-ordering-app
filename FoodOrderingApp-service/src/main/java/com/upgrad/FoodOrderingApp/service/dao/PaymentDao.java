package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;

import java.util.List;

/*
 * This PaymentDao interface gives the list of all the dao methods that exist in the payment dao implementation class.
 * Service class will be calling the dao methods by this interface.
 */
public interface PaymentDao {

    List<PaymentEntity> getAllPaymentMethods();
    PaymentEntity getPaymentByUUID(String paymentId);
}
