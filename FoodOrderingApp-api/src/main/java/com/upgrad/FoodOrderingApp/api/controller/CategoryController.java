package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * A controller method to get all address from the database.
     *
     * @param categoryId - The uuid of the category whose detail is asked from the database..
     * @return - ResponseEntity<CategoryDetailsResponse> type object along with Http status OK.
     * @throws CategoryNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable("category_id") String categoryId) throws CategoryNotFoundException {

        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);
        List<ItemEntity> itemEntityList = categoryEntity.getItems();
        List<ItemList> itemLists = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntityList){
            ItemList itemList = new ItemList().id(UUID.fromString(itemEntity.getUuid())).itemName(itemEntity.getItemName()).price(itemEntity.getPrice()).
                    itemType(ItemList.ItemTypeEnum.valueOf(itemEntity.getType().name()));
            itemLists.add(itemList);
        }
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse().id(UUID.fromString(categoryEntity.getUuid())).categoryName(categoryEntity.getCategoryName()).itemList(itemLists);
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }

    /**
     * A controller method to get all categories from the database.
     *
     * @return - ResponseEntity<CategoriesListResponse> type object along with Http status OK.
     */
    @RequestMapping(method = RequestMethod.GET, path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {

        List<CategoryEntity> categoryEntityList = categoryService.getAllCategoriesOrderedByName();
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();
        for (CategoryEntity categoryEntity : categoryEntityList){
            CategoryListResponse categoryListResponse = new CategoryListResponse().id(UUID.fromString(categoryEntity.getUuid())).categoryName(categoryEntity.getCategoryName());
            categoriesListResponse.addCategoriesItem(categoryListResponse);
        }
        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }
}
