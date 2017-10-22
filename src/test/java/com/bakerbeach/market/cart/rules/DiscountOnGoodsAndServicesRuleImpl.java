package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.commons.Message;
import com.bakerbeach.market.commons.MessageImpl;
import com.bakerbeach.market.core.api.model.Cart;

public class DiscountOnGoodsAndServicesRuleImpl extends AbstractCartRuleImpl {

	@Override
	public CartRuleResult apply(Cart cart, Intention intention, CartRuleContext context, Map<String, CartRuleResult> results) {
		CartRuleResult result = new SimpleCartRuleResult("discountOnGoodsAndServicesTest");

		if (intentions.contains(intention)) {
			result.getValues().put("total", new BigDecimal("-3.00"));
			result.getMessages().add(new MessageImpl("discount-2", Message.TYPE_INFO, "discount",
					Arrays.asList(Message.TAG_BOX), Arrays.asList(new BigDecimal("-3.00"))));
		}

		return result;
	}

}
