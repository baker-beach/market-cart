package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.cart.api.model.CartRuleError;
import com.bakerbeach.market.cart.api.model.CartRuleResult;

public class SimpleCartRuleResult implements CartRuleResult {
	private Map<String, BigDecimal> discounts = new HashMap<String, BigDecimal>();
	private List<CartRuleError> errors = new ArrayList<CartRuleError>();

	@Override
	public Map<String, BigDecimal> getDiscounts() {
		return discounts;
	}

	@Override
	public List<CartRuleError> getErrors() {
		return errors;
	}

	public void addError(CartRuleError error) {
		errors.add(error);
	}

	@Override
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

}
