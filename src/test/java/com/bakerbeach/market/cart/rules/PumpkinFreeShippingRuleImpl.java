package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.Arrays;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.commons.Message;
import com.bakerbeach.market.commons.MessageImpl;
import com.bakerbeach.market.core.api.model.Cart;

public class PumpkinFreeShippingRuleImpl extends AbstractCartRuleImpl {
	private BigDecimal limit = BigDecimal.ZERO;

	@Override
	public CartRuleResult apply(Cart cart, Intention intention, CartRuleContext context) {
		CartRuleResult result = new SimpleCartRuleResult();

		if (intention.equals(this.getIntention())) {
			result.getValues().put("total", new BigDecimal("-3.95"));
			result.getMessages().add(new MessageImpl(Message.TYPE_INFO, "shipping-true", Arrays.asList(Message.TAG_CART)));
		}

		return result;
	}

	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit = limit;
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
