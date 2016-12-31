package com.bakerbeach.market.cart.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.CartItemQualifier;
import com.bakerbeach.market.core.api.model.TaxCode;

public class ShippingCartItem extends AbstractCartItem implements CartItem {
	private static final String DEFAULT_GTIN = "shipping";
	private String qualifier = CartItemQualifier.SHIPPING;
	
	public ShippingCartItem(BigDecimal unitPrice, TaxCode taxCode) {
		init();
		this.unitPrice = unitPrice;
		this.taxCode = taxCode;
	}
	
	private void init() {
		this.gtin = DEFAULT_GTIN;
		this.quantity = BigDecimal.ONE;		
		this.isVisible = false;
		this.isVolatile = true;
		this.isImmutable = true;
	}
	
	@Override
	public String getQualifier() {
		return qualifier;
	}

	@Override
	public Map<String, CartItemComponent> getComponents() {
		return new HashMap<String, CartItemComponent>();
	}

}
