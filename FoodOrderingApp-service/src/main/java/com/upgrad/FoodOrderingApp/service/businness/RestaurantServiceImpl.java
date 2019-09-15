package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * The method implements the business logic for getting restaurant details by restaurant uuid.
     */
    @Override
    public RestaurantEntity restaurantByUUID(String restaurantId) throws RestaurantNotFoundException {
        if(restaurantId.isEmpty()) throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        else if(restaurantDao.restaurantByUUID(restaurantId)==null)
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        else return restaurantDao.restaurantByUUID(restaurantId);
    }

    /**
     * The method implements the business logic for getting restaurants by restaurant name.
     */
    @Override
    public List<RestaurantEntity> restaurantsByName(String restaurantName) throws RestaurantNotFoundException {
        if(restaurantName.isEmpty()) throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        else return restaurantDao.restaurantsByName(restaurantName);
    }

    /**
     * The method implements the business logic for getting all restaurants ordered by their rating.
     */
    @Override
    public List<RestaurantEntity> restaurantsByRating() {
        return restaurantDao.restaurantsByRating();
    }

    /**
     * The method implements the business logic for getting restaurants by their category.
     */
    @Override
    public List<RestaurantEntity> restaurantByCategory(String categoryId) throws CategoryNotFoundException {
        if(categoryId.isEmpty()) throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        else if(categoryDao.getCategoryById(categoryId)==null)
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        else return restaurantDao.restaurantByCategory(categoryId);
    }

    /**
     * The method implements the business logic for updating restaurant rating endpoint.
     */
    @Override
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity, Double customerRating) throws InvalidRatingException {
        if(customerRating==null || customerRating>5 || customerRating<1)
            throw new InvalidRatingException("IRE-001","Rating should be in the range of 1 to 5");
        Double oldRating = restaurantEntity.getCustomerRating();
        Integer numberOfUserRated = restaurantEntity.getNumberCustomersRated()+1;
        Double updatedRating = (oldRating*(numberOfUserRated-1) + customerRating)/numberOfUserRated;
        restaurantEntity.setNumberCustomersRated(numberOfUserRated);
        restaurantEntity.setCustomerRating(updatedRating);
        return restaurantDao.updateRestaurantRating(restaurantEntity);
    }
}
