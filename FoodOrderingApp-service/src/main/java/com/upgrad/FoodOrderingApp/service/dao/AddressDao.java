package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;

import javax.persistence.TypedQuery;
import java.util.List;

/*
 * This AddressDao interface gives the list of all the dao methods that exist in the address dao implementation class.
 * Service class will be calling the dao methods by this interface.
 */
public interface AddressDao {

    AddressEntity saveAddress(AddressEntity addressEntity);
    AddressEntity getAddressByUUID(String addressId);
    CustomerAddressEntity getCustomerByAddress(String addressId);
    CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity);
    AddressEntity updateAddress(AddressEntity addressEntity);
    AddressEntity deleteAddress(AddressEntity addressEntity);
    List<AddressEntity> getAllAddress(CustomerEntity customer);
    List<StateEntity> getAllStates();
    StateEntity getStateByUUID(String uuid);
}
