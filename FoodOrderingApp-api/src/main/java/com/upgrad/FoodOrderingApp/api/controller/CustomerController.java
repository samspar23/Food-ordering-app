package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import com.upgrad.FoodOrderingApp.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * A controller method for customer signup.
     *
     * @param signupCustomerRequest - This argument contains all the attributes required to store customer details in the database.
     * @return - ResponseEntity<SignupCustomerResponse> type object along with Http status CREATED.
     * @throws SignUpRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        if(signupCustomerRequest.getContactNumber().isEmpty()||signupCustomerRequest.getEmailAddress().isEmpty()||signupCustomerRequest.getFirstName().isEmpty()||signupCustomerRequest.getPassword().isEmpty())
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        final CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        final CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(createdCustomerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
    }


    /**
     * A controller method for customer authentication.
     *
     * @param authorization - A field in the request header which contains the customer credentials as Basic authentication.
     * @return - ResponseEntity<LoginResponse> type object along with Http status OK.
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        if(decodedText.matches(":")==Boolean.TRUE) throw new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        CustomerAuthEntity createdCustomerAuthEntity = customerService.authenticate(decodedArray[0], decodedArray[1]);
        CustomerEntity customerEntity = createdCustomerAuthEntity.getCustomer();
        LoginResponse loginResponse = new LoginResponse().id(customerEntity.getUuid()).message("LOGGED IN SUCCESSFULLY").firstName(customerEntity.getFirstName()).lastName(customerEntity.getLastName()).contactNumber(customerEntity.getContactNumber()).emailAddress(customerEntity.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", createdCustomerAuthEntity.getAccessToken());
        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }


    /**
     * A controller method for customer logout.
     *
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<LogoutResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String access_token = authorization.split("Bearer ")[1];
        CustomerAuthEntity customerAuthEntity = customerService.logout(access_token);

        LogoutResponse logoutResponse = new LogoutResponse().id(customerAuthEntity.getCustomer().getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    /**
     * A controller method for updating customer details.
     *
     * @param updateCustomerRequest - This argument contains all the attributes required to update customer details in the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<LogoutResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomerDetails(@RequestBody(required = false) final UpdateCustomerRequest updateCustomerRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UpdateCustomerException {
        if(updateCustomerRequest.getFirstName().isEmpty())
            throw new UpdateCustomerException("UCR-002","First name field should not be empty");
        String access_token = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(access_token);
        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        if(updateCustomerRequest.getLastName()!=null) customerEntity.setLastName(updateCustomerRequest.getLastName());
        customerEntity = customerService.updateCustomer(customerEntity);
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(customerEntity.getUuid()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY").firstName(customerEntity.getFirstName()).lastName(customerEntity.getLastName());
        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    /**
     * A controller method for updating customer password.
     *
     * @param updatePasswordRequest - This argument contains all the attributes required to update customer password in the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<LogoutResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/password", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updateCustomerPassword(@RequestBody(required = false) final UpdatePasswordRequest updatePasswordRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UpdateCustomerException {
        if(updatePasswordRequest.getNewPassword().isEmpty()||updatePasswordRequest.getOldPassword().isEmpty())
            throw new UpdateCustomerException("UCR-003","No field should be empty");
        String access_token = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(access_token);
        customerEntity = customerService.updateCustomerPassword(updatePasswordRequest.getOldPassword(),updatePasswordRequest.getNewPassword(),customerEntity);
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(customerEntity.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }

}
