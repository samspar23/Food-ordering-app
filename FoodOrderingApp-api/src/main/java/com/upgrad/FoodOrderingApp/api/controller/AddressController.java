package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class AddressController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    /**
     * A controller method to save an address in the database.
     *
     * @body SaveAddressRequest - This argument contains all the attributes required to store address details in the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<SaveAddressResponse> type object along with Http status CREATED.
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestBody(required = false) final SaveAddressRequest saveAddressRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {
        String access_token = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(access_token);
        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));
        addressEntity.setUuid(UUID.randomUUID().toString());

        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(addressEntity);
        customerAddressEntity.setCustomer(customerEntity);

        final AddressEntity createdAddressEntity = addressService.saveAddress(addressEntity,customerAddressEntity);
        SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(createdAddressEntity.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    /**
     * A controller method to delete an address from the database.
     *
     * @param addressId    - The uuid of the address to be deleted from the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<DeleteAddressResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@PathVariable("address_id") final String addressId,  @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AddressNotFoundException {
        String access_token = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(access_token);
        if(addressId==null) throw new AddressNotFoundException("ANF-005","Address id can not be empty");
        final AddressEntity addressEntity = addressService.getAddressByUUID(addressId,customerEntity);

        final AddressEntity deletedAddressEntity = addressService.deleteAddress(addressEntity);
        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(deletedAddressEntity.getUuid())).status("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);
    }

    /**
     * A controller method to get all address from the database.
     *
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<AddressListResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllAddresses(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String access_token = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(access_token);
        List<AddressEntity> addressEntityList = addressService.getAllAddress(customerEntity);
        AddressListResponse addressListResponse = new AddressListResponse();
        for (AddressEntity addressEntity : addressEntityList){
            AddressList addressList = new AddressList();
            addressList.setId(UUID.fromString(addressEntity.getUuid()));
            addressList.setLocality(addressEntity.getLocality());
            addressList.setCity(addressEntity.getCity());
            addressList.setFlatBuildingName(addressEntity.getFlatBuilNo());
            addressList.setPincode(addressEntity.getPincode());
            AddressListState addressListState = new AddressListState();
            addressListState.setId(UUID.fromString(addressEntity.getState().getUuid()));
            addressListState.setStateName(addressEntity.getState().getStateName());
            addressList.setState(addressListState);
            addressListResponse.addAddressesItem(addressList);
        }
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    /**
     * A controller method to get all states from the database.
     *
     * @return - ResponseEntity<StatesListResponse> type object along with Http status OK.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates(){
        List<StateEntity> stateEntityList = addressService.getAllStates();
        StatesListResponse statesListResponse = new StatesListResponse();
        for (StateEntity stateEntity : stateEntityList) {
            StatesList statesList = new StatesList();
            statesList.setId(UUID.fromString(stateEntity.getUuid()));
            statesList.setStateName(stateEntity.getStateName());
            statesListResponse.addStatesItem(statesList);
        }
        return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
    }

}
