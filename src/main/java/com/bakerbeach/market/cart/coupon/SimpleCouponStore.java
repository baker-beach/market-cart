package com.bakerbeach.market.cart.coupon;

import java.util.HashMap;
import java.util.Map;

import com.bakerbeach.market.core.api.model.Coupon;

public class SimpleCouponStore implements CouponStore {
	private Map<String, Coupon> coupons = new HashMap<String, Coupon>();

	@Override
	public Coupon getCoupon(String couponCode) {
		Coupon coupon = coupons.get(couponCode.toLowerCase().trim());
		if (coupon != null) {
			return coupon.getInstance();			
		} else {
			return null;
		}
	}

	public void setCoupons(Map<String, Coupon> coupons) {
		this.coupons = coupons;
	}

}
