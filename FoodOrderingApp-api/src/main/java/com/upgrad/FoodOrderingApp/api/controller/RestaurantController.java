package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CustomerService customerService;

    /**
     * A controller method to get a restaurant details from the database.
     *
     * @param restaurantId - The uuid of the restaurant whose details has to be fetched from the database.
     * @return - ResponseEntity<RestaurantDetailsResponse> type object along with Http status OK.
     * @throws RestaurantNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantDetails(@PathVariable("restaurant_id") String restaurantId) throws RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);
        List<CategoryEntity> restaurantCategoryEntityList = categoryService.getCategoriesByRestaurant(restaurantId);
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).stateName(restaurantEntity.getAddress().getState().getStateName());
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress().id(UUID.fromString(restaurantEntity.getAddress().getUuid())).flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo()).city(restaurantEntity.getAddress().getCity()).
                locality(restaurantEntity.getAddress().getLocality()).pincode(restaurantEntity.getAddress().getPincode()).state(restaurantDetailsResponseAddressState);
        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse().id(UUID.fromString(restaurantEntity.getUuid())).restaurantName(restaurantEntity.getRestaurantName()).
                averagePrice(restaurantEntity.getAvgPrice()).customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating())).numberCustomersRated(restaurantEntity.getNumberCustomersRated()).
                photoURL(restaurantEntity.getPhotoUrl()).address(restaurantDetailsResponseAddress);
        for (CategoryEntity categoryEntity : restaurantCategoryEntityList) {
            CategoryList categoryList = new CategoryList().id(UUID.fromString(categoryEntity.getUuid())).categoryName(categoryEntity.getCategoryName());
            List<ItemEntity> categoryItemEntities = itemService.getItemsByCategoryAndRestaurant(restaurantId, categoryEntity.getUuid());
            for (ItemEntity itemEntity : categoryItemEntities) {
                ItemList itemList = new ItemList().id(UUID.fromString(itemEntity.getUuid())).itemName(itemEntity.getItemName()).itemType(ItemList.ItemTypeEnum.valueOf(itemEntity.getType().name())).
                        price(itemEntity.getPrice());
                categoryList.addItemListItem(itemList);
            }
            restaurantDetailsResponse.addCategoriesItem(categoryList);
        }
        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }

    /**
     * A controller method to get restaurant details by its name from the database.
     *
     * @param restaurantName - The name of the restaurant whose details has to be fetched from the database.
     * @return - ResponseEntity<RestaurantListResponse> type object along with Http status OK.
     * @throws RestaurantNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByName(@PathVariable("restaurant_name") String restaurantName) throws RestaurantNotFoundException {

        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByName(restaurantName);
        RestaurantListResponse restaurantListResponse = getRestaurantListResponse(restaurantEntityList);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * A controller method to get all restaurant by a category from the database.
     *
     * @param categoryId - The uuid of the category under which the restaurant list has to be fetched from the database.
     * @return - ResponseEntity<RestaurantListResponse> type object along with Http status OK.
     * @throws CategoryNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantByCategory(@PathVariable("category_id") String categoryId) throws CategoryNotFoundException {

        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantByCategory(categoryId);
        RestaurantListResponse restaurantListResponse = getRestaurantListResponse(restaurantEntityList);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * A controller method to get all restaurant from the database.
     *
     * @return - ResponseEntity<RestaurantListResponse> type object along with Http status OK.
     */
    @RequestMapping(method = RequestMethod.GET, path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurant() {

        List<RestaurantEntity> restaurantEntityList = restaurantService.restaurantsByRating();
        RestaurantListResponse restaurantListResponse = getRestaurantListResponse(restaurantEntityList);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * A controller method to update restaurant rating in the database.
     *
     * @param restaurantId   - The uuid of the restaurant whose rating is to be updated in the database.
     * @param customerRating - The rating given by customer.
     * @param authorization  - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<RestaurantUpdatedResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws RestaurantNotFoundException
     * @throws InvalidRatingException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantRating(@PathVariable("restaurant_id") String restaurantId, @RequestParam("customer_rating") Double customerRating, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {
        String access_token = authorization.split("Bearer ")[1];
        customerService.getCustomer(access_token);
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);
        restaurantService.updateRestaurantRating(restaurantEntity, customerRating);
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse();
        restaurantUpdatedResponse.setId(UUID.fromString(restaurantId));
        restaurantUpdatedResponse.setStatus("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    private RestaurantListResponse getRestaurantListResponse(List<RestaurantEntity> restaurantEntityList) {
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        for (RestaurantEntity restaurantEntity : restaurantEntityList) {
            List<CategoryEntity> restaurantCategoryEntityList = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).stateName(restaurantEntity.getAddress().getState().getStateName());
            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress().id(UUID.fromString(restaurantEntity.getAddress().getUuid())).flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo()).city(restaurantEntity.getAddress().getCity()).
                    locality(restaurantEntity.getAddress().getLocality()).pincode(restaurantEntity.getAddress().getPincode()).state(restaurantDetailsResponseAddressState);
            String categoryString = "";
            for (CategoryEntity categoryEntity : restaurantCategoryEntityList) {
                categoryString = categoryString + categoryEntity.getCategoryName() + ", ";
            }
            categoryString = categoryString.substring(0, categoryString.length() - 2);
            RestaurantList restaurantList = new RestaurantList().id(UUID.fromString(restaurantEntity.getUuid())).restaurantName(restaurantEntity.getRestaurantName()).
                    averagePrice(restaurantEntity.getAvgPrice()).customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating())).numberCustomersRated(restaurantEntity.getNumberCustomersRated()).
                    photoURL(restaurantEntity.getPhotoUrl()).address(restaurantDetailsResponseAddress).categories(categoryString);
            restaurantListResponse.addRestaurantsItem(restaurantList);
        }
        return restaurantListResponse;
    }


}
