package com.nhnacademy.coupon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class FixedCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @MapsId
    @OneToOne
    private CouponType couponType;

    private int discountPrice;

    public FixedCoupon(CouponType couponType, int discountPrice) {
        this.couponType = couponType;
        this.discountPrice = discountPrice;
    }
}
