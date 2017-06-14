package com.bakerbeach.market.cart.rules;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.service.CartRulesAware;
import com.bakerbeach.market.core.api.model.Cart;

public class SimpleCartRuleContextImpl implements CartRuleContext {

	private String shopCode;
	private String customerId;
	private String customerEmail;
	private Cart cart;

	@Override
	public String getShopCode() {
		return shopCode;
	}

	@Override
	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	@Override
	public String getCustomerId() {
		return customerId;
	}

	@Override
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Override
	public String getCustomerEmail() {
		return customerEmail;
	}

	@Override
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	@Override
	public Cart getCart() {
		return cart;
	}

	@Override
	public void setCart(Cart cart) {
		this.cart = cart;
	}
	
	@Override
	public CartRulesAware getCartRuleAware() {
		if (cart instanceof CartRulesAware) {
			return (CartRulesAware) cart;
		} else {
			return null;			
		}
	}

}
