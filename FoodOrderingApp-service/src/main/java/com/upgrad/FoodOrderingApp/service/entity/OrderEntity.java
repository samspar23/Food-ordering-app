package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OrderEntity class contains all the attributes to be mapped to all the fields in orders table in the database.
 * All the annotations which are used to specify all the constraints to the columns in the database must be correctly implemented.
 */
@Entity
@Table(name = "orders")
@NamedQueries({
        @NamedQuery(name = "ordersByCustomer", query = "select o from OrderEntity o where o.customer.uuid = :customerId order by o.date desc"),
        @NamedQuery(name = "getAllOrdersByAddressUUID", query = "select o from OrderEntity o inner join o.address a where " +
                "a.uuid = :uuid")
})
public class OrderEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID", length = 64, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private Double bill;

    @OneToOne
    private CouponEntity coupon;

    @Column
    private Double discount;

    @Column(nullable = false)
    private Date date = new Date();

    @OneToOne
    @NotNull
    private PaymentEntity payment;

    @ManyToOne
    @NotNull
    private CustomerEntity customer;

    @OneToOne
    @NotNull
    private AddressEntity address;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @NotNull
    private RestaurantEntity restaurant;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> orderItemEntities = new ArrayList<>();

    public OrderEntity() {
    }

    public OrderEntity(String uuid, Double bill, CouponEntity coupon, Double discount, Date date, @NotNull PaymentEntity payment, @NotNull CustomerEntity customer, @NotNull AddressEntity address, @NotNull RestaurantEntity restaurant) {
        this.uuid = uuid;
        this.bill = bill;
        this.coupon = coupon;
        this.discount = discount;
        this.date = date;
        this.payment = payment;
        this.customer = customer;
        this.address = address;
        this.restaurant = restaurant;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Double getBill() {
        return bill;
    }

    public void setBill(Double bill) {
        this.bill = bill;
    }

    public CouponEntity getCoupon() {
        return coupon;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public void setCoupon(CouponEntity coupon) {
        this.coupon = coupon;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public List<OrderItemEntity> getOrderItemEntities() {
        return orderItemEntities;
    }

    public void setOrderItemEntities(List<OrderItemEntity> orderItemEntities) {
        this.orderItemEntities = orderItemEntities;
    }
}
