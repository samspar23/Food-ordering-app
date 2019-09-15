package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private OrderDao orderDao;

    /**
     * The method implements the business logic for save address endpoint.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity,CustomerAddressEntity customerAddressEntity) throws SaveAddressException {
        if(addressEntity.getFlatBuilNo().isEmpty()||addressEntity.getCity().isEmpty()||addressEntity.getLocality().isEmpty()||addressEntity.getPincode().isEmpty()||addressEntity.getUuid().isEmpty())
            throw new SaveAddressException("SAR-001", "No field can be empty");
        else if (!addressEntity.getPincode().matches("[0-9]+") || addressEntity.getPincode().length() != 6)
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        addressEntity.setActive(1);
        addressEntity = addressDao.saveAddress(addressEntity);
        saveCustomerAddress(customerAddressEntity);
        return addressEntity;
    }

    /**
     * The method implements the business logic for get address by uuid endpoint.
     */
    @Override
    public AddressEntity getAddressByUUID(String addressId,CustomerEntity customerEntity) throws  AuthorizationFailedException, AddressNotFoundException {
        if(addressDao.getAddressByUUID(addressId) == null) throw new AddressNotFoundException("ANF-003", "No address by this id");
        else if(addressDao.getCustomerByAddress(addressId).getCustomer() != customerEntity) throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        return addressDao.getAddressByUUID(addressId);
    }


    /**
     * The method implements the business logic for saving customer address.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity) {
        return addressDao.saveCustomerAddress(customerAddressEntity);
    }

    /**
     * The method implements the business logic for delete address endpoint.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        if (orderDao.getAllOrdersByAddressUUID(addressEntity.getUuid()).isEmpty()) {
            return addressDao.deleteAddress(addressEntity);
        }
        else {
            addressEntity.setActive(0);
            addressDao.updateAddress(addressEntity);
            return addressEntity;
        }
    }

    /**
     * The method implements the business logic for getting all saved address endpoint.
     */
    @Override
    public List<AddressEntity> getAllAddress(CustomerEntity customer)  {
        return addressDao.getAllAddress(customer);
    }

    /**
     * The method implements the business logic for getting state by id.
     */
    @Override
    public StateEntity getStateByUUID(String uuid) throws AddressNotFoundException {
        if(addressDao.getStateByUUID(uuid)==null)
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        return addressDao.getStateByUUID(uuid);
    }

    /**
     * The method implements the business logic for getting list of all states endpoint.
     */
    @Override
    public List<StateEntity> getAllStates() {
        return addressDao.getAllStates();
    }


}
