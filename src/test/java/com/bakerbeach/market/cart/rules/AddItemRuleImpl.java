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
import com.bakerbeach.market.core.api.model.CartItemQualifier;
import com.bakerbeach.market.core.api.model.TaxCode;

public class AddItemRuleImpl extends AbstractCartRuleImpl {

	@Override
	public void apply(Cart cart, Intention intention, CartRuleContext context, Map<String, CartRuleResult> results) {
		CartRuleResult result = new SimpleCartRuleResult("addItemTest");

		try {
			if (useCount < 1) {
				result.put("gtin", "42424242");
				result.put("qualifier", CartItemQualifier.PRODUCT);
				result.put("taxCode", TaxCode.REDUCED);
				result.put("quantity", BigDecimal.ONE);
				result.put("stdUnitPrice", new BigDecimal(".38"));

				addSuccessMessage(result);
			} else {
				addErrorMessage(result);
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		results.put(result.getId(), result);
	}

	protected void addErrorMessage(CartRuleResult result) {
	}

	protected void addSuccessMessage(CartRuleResult result) {
		result.getMessages().add(
				new MessageImpl("rule-1", Message.TYPE_INFO, "extra item", null, Arrays.asList(result.get("gtin"))));
	}

}
