package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * ItemDao class provides the database access for all the endpoints in Item controller.
 */
@Repository
public class ItemDaoImpl implements ItemDao{

    //When a container of the application(be it a Java EE container or any other custom container like Spring) manages the lifecycle of the Entity Manager, the Entity Manager is said to be Container Managed. The most common way of acquiring a Container Managed EntityManager is to use @PersistenceContext annotation on an EntityManager attribute.
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("getItemsByPopularity", ItemEntity.class).setParameter("restaurant", restaurantEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }


    @Override
    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId) {
        try {
            return entityManager.createNamedQuery("getItemsByCategoryAndRestaurant", ItemEntity.class).setParameter("restaurantId", restaurantId).setParameter("categoryId", categoryId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public ItemEntity getItemByUUID(String itemId) {
        try {
            return entityManager.createNamedQuery("getItemByUUID", ItemEntity.class).setParameter("itemId", itemId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
