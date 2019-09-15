package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;

import java.util.List;

/*
 * This RestaurantService interface gives the list of all the service that exist in the restaurant service implementation class.
 * Controller class will be calling the service methods by this interface.
 */
public interface RestaurantService {

    RestaurantEntity restaurantByUUID(String restaurantId) throws RestaurantNotFoundException;
    List<RestaurantEntity> restaurantsByName(String restaurantName) throws RestaurantNotFoundException;
    List<RestaurantEntity> restaurantsByRating();
    List<RestaurantEntity> restaurantByCategory(String categoryId) throws CategoryNotFoundException;
    RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity, Double customerRating) throws InvalidRatingException;
}
