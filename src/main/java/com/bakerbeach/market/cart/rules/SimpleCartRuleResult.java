package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bakerbeach.market.cart.api.model.CartRuleMessage;
import com.bakerbeach.market.cart.api.model.CartRuleResult;

public class SimpleCartRuleResult implements CartRuleResult {
	private Map<String, BigDecimal> discounts = new HashMap<String, BigDecimal>();
	private CartRuleMessage message;

	@Override
	public Map<String, BigDecimal> getDiscounts() {
		return discounts;
	}

	@Override
	public CartRuleMessage getMessage() {
		return message;
	}

	@Override
	public void setMessage(CartRuleMessage message) {
		this.message = message;
	}

	@Override
	public boolean hasError() {
		return message.getType().equals(CartRuleMessage.Type.ERROR);
	}

}
