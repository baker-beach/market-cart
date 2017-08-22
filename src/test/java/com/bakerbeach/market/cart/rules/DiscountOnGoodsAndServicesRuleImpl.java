package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.core.api.model.Cart;

public class DiscountOnGoodsAndServicesRuleImpl extends AbstractCartRuleImpl {

	@Override
	public CartRuleResult apply(Cart cart, Intention intention, CartRuleContext context) {
		CartRuleResult result = new SimpleCartRuleResult();

		if (intention.equals(this.getIntention())) {
			result.getValues().put("total", new BigDecimal("-3.00"));
		}

		return result;
	}

	@Override
	protected void addSuccessMessage(CartRuleResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addErrorMessage(CartRuleResult result) {
		// TODO Auto-generated method stub

	}

}
