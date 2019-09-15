package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;

import java.util.List;

/*
 * This PaymentService interface gives the list of all the service that exist in the payment service implementation class.
 * Controller class will be calling the service methods by this interface.
 */
public interface PaymentService {

    List<PaymentEntity> getAllPaymentMethods() ;
    PaymentEntity getPaymentByUUID(String paymentId) throws PaymentMethodNotFoundException;
}
