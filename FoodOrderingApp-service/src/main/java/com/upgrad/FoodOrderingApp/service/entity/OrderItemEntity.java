package com.upgrad.FoodOrderingApp.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * OrderItemEntity class contains all the attributes to be mapped to all the fields in order_item table in the database.
 * All the annotations which are used to specify all the constraints to the columns in the database must be correctly implemented.
 */
@Entity
@Table(name = "order_item")
public class OrderItemEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column
    private Integer quantity;

    @Column
    private Integer price;

    public OrderItemEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }
}
