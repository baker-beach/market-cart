package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.RuleContext;
import com.bakerbeach.market.cart.api.model.RuleResult;
import com.bakerbeach.market.core.api.model.Cart;

public class DiscountOnGoodsAndServicesRuleImpl extends AbstractCartRuleImpl {

	@Override
	public RuleResult apply(Cart cart, Intention intention, CartRuleContext context) {
		RuleResult result = new SimpleCartRuleResult();

		if (intention.equals(this.getIntention())) {
			result.getValues().put("total", new BigDecimal("-3.00"));
		}

		return result;
	}

	@Override
	public RuleResult apply(RuleContext context) {
		// TODO Auto-generated method stub
		return null;
	}

}
