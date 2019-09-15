package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    /**
     * The method implements the business logic for getting top 5 items by popularity.
     */
    @Override
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        return itemDao.getItemsByPopularity(restaurantEntity);
    }

    /**
     * The method implements the business logic for getting list of items based on restaurant and category uuid.
     */
    @Override
    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId) {
        return itemDao.getItemsByCategoryAndRestaurant(restaurantId,categoryId);
    }

    /**
     * The method implements the business logic for getting item details by item uuid.
     */
    @Override
    public ItemEntity getItemByUUID(String itemId) throws ItemNotFoundException {
        if (itemDao.getItemByUUID(itemId)==null)
            throw new ItemNotFoundException("INF-003","No item by this id exist");
        return itemDao.getItemByUUID(itemId);
    }

}
