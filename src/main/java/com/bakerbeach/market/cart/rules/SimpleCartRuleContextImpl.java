package com.bakerbeach.market.cart.rules;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.service.RuleAware;
import com.bakerbeach.market.core.api.model.Address;
import com.bakerbeach.market.core.api.model.Cart;

public class SimpleCartRuleContextImpl extends SimpleRuleContextImpl implements CartRuleContext {

	private String shopCode;
	private String customerId;
	private String customerEmail;
	private Address shippingAddress;
	private Cart cart;

//	@Override
//	public String getShopCode() {
//		return shopCode;
//	}
//
//	@Override
//	public void setShopCode(String shopCode) {
//		this.shopCode = shopCode;
//	}
//
//	@Override
//	public String getCustomerId() {
//		return customerId;
//	}
//
//	@Override
//	public void setCustomerId(String customerId) {
//		this.customerId = customerId;
//	}
//
//	@Override
//	public String getCustomerEmail() {
//		return customerEmail;
//	}
//
//	@Override
//	public void setCustomerEmail(String customerEmail) {
//		this.customerEmail = customerEmail;
//	}
//
//	@Override
//	public Cart getCart() {
//		return cart;
//	}
//
//	@Override
//	public void setCart(Cart cart) {
//		this.cart = cart;
//	}
//
//	@Override
//	public Address getShippingAddress() {
//		return shippingAddress;
//	}
//
//	@Override
//	public void setShippingAddress(Address shippingAddress) {
//		this.shippingAddress = shippingAddress;
//	}
//
//	@Override
//	public RuleAware getCartRuleAware() {
//		if (cart instanceof RuleAware) {
//			return (RuleAware) cart;
//		} else {
//			return null;
//		}
//	}

}
