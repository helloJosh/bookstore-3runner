package com.nhnacademy.coupon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RatioCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @MapsId
    @OneToOne
    private CouponType couponType;

    private double discountRate;
    private int discountMaxPrice;

    public RatioCoupon(CouponType couponType, double discountRate, int discountMaxPrice) {
        this.couponType = couponType;
        this.discountRate = discountRate;
        this.discountMaxPrice = discountMaxPrice;
    }
}
