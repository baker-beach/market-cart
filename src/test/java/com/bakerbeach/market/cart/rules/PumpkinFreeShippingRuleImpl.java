package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.commons.Message;
import com.bakerbeach.market.commons.MessageImpl;
import com.bakerbeach.market.core.api.model.Cart;

public class PumpkinFreeShippingRuleImpl extends AbstractCartRuleImpl {
	private BigDecimal limit = BigDecimal.ZERO;

	@Override
	public CartRuleResult apply(Cart cart, Intention intention, CartRuleContext context, Map<String, CartRuleResult> results) {
		CartRuleResult result = new SimpleCartRuleResult("pumpkinFreeShippingTest");

		if (intentions.contains(intention)) {
			result.getValues().put("total", new BigDecimal("-3.95"));
			result.getMessages().add(new MessageImpl("shipping", Message.TYPE_INFO, "shipping-true",
					Arrays.asList(Message.TAG_CART), new ArrayList<Object>()));
		}

		return result;
	}

	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}

}
