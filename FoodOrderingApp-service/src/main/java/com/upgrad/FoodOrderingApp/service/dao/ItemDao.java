package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;

import java.util.List;

/*
 * This ItemDao interface gives the list of all the dao methods that exist in the item dao implementation class.
 * Service class will be calling the dao methods by this interface.
 */
public interface ItemDao {

    List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity);
    List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId);
    ItemEntity getItemByUUID(String itemId);
}
