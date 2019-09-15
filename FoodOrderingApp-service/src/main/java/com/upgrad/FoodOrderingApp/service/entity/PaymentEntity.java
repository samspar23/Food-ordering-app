package com.upgrad.FoodOrderingApp.service.entity;


import javax.persistence.*;
import java.io.Serializable;

/**
 * PaymentEntity class contains all the attributes to be mapped to all the fields in payment table in the database.
 * All the annotations which are used to specify all the constraints to the columns in the database must be correctly implemented.
 */
@Entity
@Table(name = "payment")
@NamedQueries({
        @NamedQuery(name = "getAllPaymentMethods", query = "select p from PaymentEntity p"),
        @NamedQuery(name = "getPaymentByUUID", query = "select p from PaymentEntity p where p.uuid = :paymentId")
})
public class PaymentEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID", length = 64, nullable = false)
    private String uuid;

    @Column(name = "payment_name",unique = true, nullable = false)
    private String paymentName;

    public PaymentEntity() {
    }

    public PaymentEntity(String uuid, String paymentName) {
        this.uuid = uuid;
        this.paymentName = paymentName;
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

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}
