package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryDao categoryDao;

    /**
     * The method implements the business logic for getting category by its id endpoint.
     */
    @Override
    public CategoryEntity getCategoryById(String categoryId) throws CategoryNotFoundException {
        if(categoryId.isEmpty()) throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        else if(categoryDao.getCategoryById(categoryId)==null)
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        else {
            return categoryDao.getCategoryById(categoryId);
        }
    }

    /**
     * The method implements the business logic for getting all categories ordered by their name endpoint.
     */
    @Override
    public List<CategoryEntity> getAllCategoriesOrderedByName()  {
        return categoryDao.getAllCategoriesOrderedByName();
    }

    /**
     * The method implements the business logic for getting categories for any particular restaurant.
     */
    @Override
    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantId)  {
        return categoryDao.getCategoriesByRestaurant(restaurantId);
    }
}
