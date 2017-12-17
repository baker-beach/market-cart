package com.bakerbeach.market.cart.rules;

import java.util.Map;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.cart.api.model.Rule.Intention;
import com.bakerbeach.market.core.api.model.Cart;

public class TestShippingRuleImpl extends AbstractRuleInst {

	@Override
	public CartRuleResult apply(Cart cart, Intention intention, CartRuleContext context, Map<String, CartRuleResult> results) {
		
		return null;
	}

}
