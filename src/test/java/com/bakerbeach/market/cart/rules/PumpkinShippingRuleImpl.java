package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.RuleContext;
import com.bakerbeach.market.cart.api.model.RuleMessage;
import com.bakerbeach.market.cart.api.model.RuleResult;
import com.bakerbeach.market.core.api.model.Address;
import com.bakerbeach.market.core.api.model.Cart;

public class PumpkinShippingRuleImpl extends AbstractCartRuleImpl {
	private BigDecimal limit = BigDecimal.ZERO;

	@Override
	public RuleResult apply(Cart cart, Intention intention, CartRuleContext context) {
		RuleResult result = new SimpleCartRuleResult();

		try {
			if (intention.equals(this.getIntention())) {
				Address shippingAddress = (Address) context.get("shippingAddress");

				BigDecimal valueOfGoods = cart.getValueOfShippingGoods();

				if (valueOfGoods.compareTo(new BigDecimal("39.00")) >= 0) {
					result.setMessage(new RuleMessage(RuleMessage.Type.INFO, "shipping-false", new BigDecimal("0")));
				} else {
					result.setMessage(new RuleMessage(RuleMessage.Type.INFO, "shipping-true", new BigDecimal("4.95")));
					result.getValues().put("total", new BigDecimal("4.95"));
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		return result;
	}

	@Override
	public RuleResult apply(RuleContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}

}
