package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.commons.Message;
import com.bakerbeach.market.commons.MessageImpl;
import com.bakerbeach.market.core.api.model.Cart;

public class PumpkinShippingRuleImpl extends AbstractCartRuleImpl {
	private BigDecimal limit = BigDecimal.ZERO;

	@Override
	public CartRuleResult apply(Cart cart, Intention intention, CartRuleContext context, Map<String, CartRuleResult> results) {
		CartRuleResult result = new SimpleCartRuleResult("shippingRuleTest");

		try {
			if (intentions.contains(intention)) {
				BigDecimal valueOfGoods = cart.getValueOfShippingGoods();

				if (valueOfGoods.compareTo(new BigDecimal("39.00")) >= 0) {
					result.getMessages().add(new MessageImpl("shipping", Message.TYPE_INFO, "shipping-false", Arrays.asList(Message.TAG_CART), Arrays.asList(new BigDecimal("0"))));
				} else {
					result.getMessages().add(new MessageImpl("shipping", Message.TYPE_INFO, "shipping-true", Arrays.asList(Message.TAG_CART), Arrays.asList(new BigDecimal("4.95"), "bar")));
					result.getValues().put("total", new BigDecimal("4.95"));
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
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
