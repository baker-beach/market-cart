package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.RuleContext;
import com.bakerbeach.market.cart.api.model.RuleResult;

public class PumpkinFreeShippingRuleImpl extends AbstractCartRuleImpl {
	private BigDecimal limit = BigDecimal.ZERO;

	@Override
	public RuleResult apply(Intention intention, CartRuleContext context) {
		RuleResult result = new SimpleCartRuleResult();
		
		if (intention.equals(this.getIntention())) {
			result.getValues().put("total", new BigDecimal("-3.95"));			
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
