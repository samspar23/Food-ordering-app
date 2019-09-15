package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * RestaurantDao class provides the database access for all the endpoints in Restaurant controller.
 */
@Repository
@Transactional
public class RestaurantDaoImpl implements RestaurantDao{

    //When a container of the application(be it a Java EE container or any other custom container like Spring) manages the lifecycle of the Entity Manager, the Entity Manager is said to be Container Managed. The most common way of acquiring a Container Managed EntityManager is to use @PersistenceContext annotation on an EntityManager attribute.
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RestaurantEntity restaurantByUUID(String restaurantId) {
        try {
            return entityManager.createNamedQuery("restaurantByUUID", RestaurantEntity.class).setParameter("uuid", restaurantId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public List<RestaurantEntity> restaurantsByName(String restaurantName) {
        try {
            return entityManager.createNamedQuery("restaurantsByName", RestaurantEntity.class).setParameter("restaurantName", restaurantName).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public List<RestaurantEntity> restaurantsByRating() {
        try {
            return entityManager.createNamedQuery("restaurantsByRating", RestaurantEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public List<RestaurantEntity> restaurantByCategory(String categoryId) {
        try {
            return entityManager.createNamedQuery("restaurantByCategory", RestaurantEntity.class).setParameter("uuid",categoryId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity) {
        return entityManager.merge(restaurantEntity);    }
}
