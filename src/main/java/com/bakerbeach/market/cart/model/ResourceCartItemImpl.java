package com.bakerbeach.market.cart.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bakerbeach.market.core.api.model.CartItemQualifier;

public class ResourceCartItemImpl extends AbstractCartItem {
	protected String qualifier = CartItemQualifier.RESOURCE;
	
	public ResourceCartItemImpl() {
		isVisible = false;
		isImmutable = true;
		isVolatile = true;
		setQuantity(BigDecimal.ONE);
		setUnitPrice(BigDecimal.ZERO);
		setTotalPrice(BigDecimal.ZERO);
		setDiscount(BigDecimal.ZERO);
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
