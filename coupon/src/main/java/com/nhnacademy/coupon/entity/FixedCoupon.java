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

//    @OneToOne
//    private CouponType couponType;

    private Long couponTypeId;

    private int discountPrice;

    public FixedCoupon(Long couponTypeId, int discountPrice) {
        this.couponTypeId = couponTypeId;
        this.discountPrice = discountPrice;
    }
}
