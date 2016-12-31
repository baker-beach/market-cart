package com.bakerbeach.market.cart.coupon;

import com.bakerbeach.market.core.api.model.Coupon;

public interface CouponStore {

	Coupon getCoupon(String couponCode);

}
