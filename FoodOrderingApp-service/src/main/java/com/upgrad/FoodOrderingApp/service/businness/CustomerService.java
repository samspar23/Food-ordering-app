package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;

/*
 * This CustomerService interface gives the list of all the service that exist in the customer service implementation class.
 * Controller class will be calling the service methods by this interface.
 */
public interface CustomerService {

    CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException;
    CustomerAuthEntity authenticate(String contactNumber, String password) throws AuthenticationFailedException;
    CustomerAuthEntity logout(String access_token) throws AuthorizationFailedException;
    void authorization(String access_token) throws AuthorizationFailedException;
    CustomerEntity updateCustomer(CustomerEntity customerEntity) throws AuthorizationFailedException;
    CustomerEntity updateCustomerPassword(String oldPassword,String newPassword, CustomerEntity customerEntity) throws UpdateCustomerException;
    CustomerEntity getCustomer(String access_token) throws AuthorizationFailedException;
}
