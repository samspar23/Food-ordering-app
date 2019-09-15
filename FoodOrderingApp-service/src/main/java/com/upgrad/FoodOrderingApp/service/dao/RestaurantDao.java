package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;

import java.util.List;

/*
 * This RestaurantDao interface gives the list of all the dao methods that exist in the restaurant dao implementation class.
 * Service class will be calling the dao methods by this interface.
 */
public interface RestaurantDao {

    RestaurantEntity restaurantByUUID(String restaurantId);
    List<RestaurantEntity> restaurantsByName(String restaurantName);
    List<RestaurantEntity> restaurantsByRating();
    List<RestaurantEntity> restaurantByCategory(String categoryId);
    RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity);
}
