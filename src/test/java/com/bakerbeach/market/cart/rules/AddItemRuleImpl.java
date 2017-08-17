package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.RuleContext;
import com.bakerbeach.market.cart.api.model.RuleResult;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItemQualifier;
import com.bakerbeach.market.core.api.model.TaxCode;

public class AddItemRuleImpl extends AbstractCartRuleImpl {

	@Override
	public RuleResult apply(Cart cart, Intention intention, CartRuleContext context) {
		RuleResult result = new SimpleCartRuleResult();
		
		try {
			if (intention.equals(this.getIntention())) {
				if (useCount < 1) {
					result.put("gtin", "42424242");
					result.put("qualifier", CartItemQualifier.PRODUCT);
					result.put("taxCode", TaxCode.REDUCED);
					result.put("quantity", BigDecimal.ONE);
					result.put("stdUnitPrice", new BigDecimal(".38"));
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

}
