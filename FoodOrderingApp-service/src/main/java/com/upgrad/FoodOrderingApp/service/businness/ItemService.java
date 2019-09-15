package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;

import java.util.List;

/*
 * This ItemService interface gives the list of all the service that exist in the item service implementation class.
 * Controller class will be calling the service methods by this interface.
 */
public interface ItemService {

    List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) ;
    List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId);
    ItemEntity getItemByUUID(String itemId) throws ItemNotFoundException;
}
