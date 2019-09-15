package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    /**
     * The method implements the business logic for getting the list of all payment methods.
     */
    @Override
    public List<PaymentEntity> getAllPaymentMethods() {
       return paymentDao.getAllPaymentMethods();
    }

    /**
     * The method implements the business logic for getting payment details by payment uuid.
     */
    @Override
    public PaymentEntity getPaymentByUUID(String paymentId) throws PaymentMethodNotFoundException {
        if (paymentDao.getPaymentByUUID(paymentId)==null)
            throw new PaymentMethodNotFoundException("PNF-002","No payment method found by this id");
        return paymentDao.getPaymentByUUID(paymentId);
    }
}
