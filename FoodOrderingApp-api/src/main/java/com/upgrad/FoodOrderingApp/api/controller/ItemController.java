package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RestaurantService restaurantService;

    /**
     * A controller method to get top 5 items by popularity from the database.
     *
     * @return - ResponseEntity<ItemListResponse> type object along with Http status OK.
     * @throws RestaurantNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getItemsByPopularity(@PathVariable("restaurant_id") String restaurantId) throws RestaurantNotFoundException {

        List<ItemEntity> itemEntityList = itemService.getItemsByPopularity(restaurantService.restaurantByUUID(restaurantId));
        ItemListResponse itemListResponse = new ItemListResponse();
        for (ItemEntity itemEntity : itemEntityList){
            ItemList itemList = new ItemList().id(UUID.fromString(itemEntity.getUuid())).itemName(itemEntity.getItemName()).price(itemEntity.getPrice()).
                    itemType(ItemList.ItemTypeEnum.valueOf(itemEntity.getType().name()));
            itemListResponse.add(itemList);
        }
        return new ResponseEntity<ItemListResponse>(itemListResponse, HttpStatus.OK);
    }
}
