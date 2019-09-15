package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * CouponEntity class contains all the attributes to be mapped to all the fields in coupon table in the database.
 * All the annotations which are used to specify all the constraints to the columns in the database must be correctly implemented.
 */
@Entity
@Table(name = "coupon")
@NamedQueries({
        @NamedQuery(name = "couponByName", query = "select c from CouponEntity c where c.couponName = :couponName"),
        @NamedQuery(name = "couponByUUID", query = "select c from CouponEntity c where c.uuid = :couponId")
})
public class CouponEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID", length = 64, nullable = false)
    private String uuid;

    @Column(name = "coupon_name", nullable = false)
    private String couponName;

    @Column(nullable = false)
    private Integer percent;

    public CouponEntity() {
    }

    public CouponEntity(String uuid, String couponName, Integer percent) {
        this.uuid = uuid;
        this.couponName = couponName;
        this.percent = percent;
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

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }
}
