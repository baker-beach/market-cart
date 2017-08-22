package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.commons.Message;
import com.bakerbeach.market.commons.MessageImpl;
import com.bakerbeach.market.core.api.model.Cart;

public class PumpkinShippingRuleImpl extends AbstractCartRuleImpl {
	private BigDecimal limit = BigDecimal.ZERO;

	@Override
	public CartRuleResult apply(Cart cart, Intention intention, CartRuleContext context) {
		CartRuleResult result = new SimpleCartRuleResult();

		try {
			if (intention.equals(this.getIntention())) {
				BigDecimal valueOfGoods = cart.getValueOfShippingGoods();

				if (valueOfGoods.compareTo(new BigDecimal("39.00")) >= 0) {
					result.getMessages().add(new MessageImpl(Message.TYPE_INFO, "shipping-false", "cart", new BigDecimal("0")));
				} else {
					result.getMessages().add(new MessageImpl(Message.TYPE_INFO, "shipping-true", "cart", new BigDecimal("4.95")));
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

	@Override
	protected void addSuccessMessage(CartRuleResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addErrorMessage(CartRuleResult result) {
		// TODO Auto-generated method stub

	}

}
