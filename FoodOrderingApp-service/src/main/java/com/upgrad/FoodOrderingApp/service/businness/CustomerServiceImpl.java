package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * The method implements the business logic for saving customer details endpoint.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(customerEntity.getEmail());
        if (!matcher.matches()) {
            throw new SignUpRestrictedException("SGR-002","Invalid email-id format!");
        } else if (!customerEntity.getContactNumber().matches("[0-9]+") || customerEntity.getContactNumber().length() != 10) {
            throw new SignUpRestrictedException("SGR-003","Invalid contact number!");
        } else if (!customerEntity.getPassword().matches(".*[0-9].*") || !customerEntity.getPassword().matches(".*[#@$%&*!^].*") ||
                !customerEntity.getPassword().matches(".*[A-Z].*") || customerEntity.getPassword().length() < 8) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);

        if (customerDao.getCustomerByContactNumber(customerEntity.getContactNumber()) != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }
        return customerDao.saveCustomer(customerEntity);
    }

    /**
     * The method implements the business logic for signin endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(String contactNumber, String password) throws AuthenticationFailedException {

        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contactNumber);

        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());

        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setUuid(UUID.randomUUID().toString());
            customerAuthEntity.setCustomer(customerEntity);
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthEntity.setLoginAt(ZonedDateTime.now());
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));

            return customerDao.createCustomerAuth(customerAuthEntity);
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }

    /**
     * The method implements the business logic for logout endpoint.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(String access_token) throws AuthorizationFailedException {

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccesstoken(access_token);
        authorization(access_token);
        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        return customerDao.updateCustomerAuth(customerAuthEntity);
    }


    /**
     * The method implements the business logic for updating customer's details endpoint.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity customerEntity)  {
        return customerDao.updateCustomer(customerEntity);
    }

    /**
     * The method implements the business logic for updating customer password endpoint.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(String oldPassword,String newPassword, CustomerEntity customerEntity) throws UpdateCustomerException {
        final String encryptedOldPassword = PasswordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
        if (encryptedOldPassword.equals(customerEntity.getPassword())) {
            if (!newPassword.matches(".*[0-9].*") || !newPassword.matches(".*[#@$%&*!^].*") ||
                    !newPassword.matches(".*[A-Z].*") || newPassword.length() < 8) {
                throw new UpdateCustomerException("UCR-001", "Weak password!");
            }
            String[] encryptedText = passwordCryptographyProvider.encrypt(newPassword);
            customerEntity.setSalt(encryptedText[0]);
            customerEntity.setPassword(encryptedText[1]);
            return customerDao.updateCustomer(customerEntity);
        }
        else throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
    }


    /**
     * The method implements the business logic for checking authorization of any customer.
     */
    @Override
    public void authorization(String access_token) throws AuthorizationFailedException {

        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccesstoken(access_token);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        if (ZonedDateTime.now().isAfter(customerAuthEntity.getExpiresAt())) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
    }

    /**
     * The method implements the business logic for getting customer details by access token.
     */
    @Override
    public CustomerEntity getCustomer(String access_token) throws AuthorizationFailedException {

        authorization(access_token);
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthByAccesstoken(access_token);
        return customerAuthEntity.getCustomer();
    }
}
