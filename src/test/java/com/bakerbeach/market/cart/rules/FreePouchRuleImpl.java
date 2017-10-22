package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.commons.Message;
import com.bakerbeach.market.commons.MessageImpl;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.CartItemQualifier;
import com.bakerbeach.market.core.api.model.TaxCode;

public class FreePouchRuleImpl extends AbstractCartRuleImpl {

	@Override
	public CartRuleResult apply(Cart cart, Intention intention, CartRuleContext context, Map<String, CartRuleResult> results) {
		CartRuleResult result = new SimpleCartRuleResult("freePouchTest");
		BigDecimal valueOfGoods = BigDecimal.ZERO; // = cart.getValueOfGoods();

		Boolean test = false;

		List<String> qualifiers = Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT);
		for (CartItem item : cart.getItems().values()) {
			try {
				if (qualifiers.contains(item.getQualifier())) {
					if (!item.getCode().equals("4260526590508") && !item.getCode().equals("std-free-pouch")) {
						test = true;
						break;
					}
				}
			} catch (Exception e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}
		}

		if (test) {
			if (!status.equals(Status.PASSED)) {				
				CartItem cartItem = cart.getNewItem("std-free-pouch", BigDecimal.ONE);
				cartItem.setId("std-free-pouch");
				cartItem.setQualifier(CartItemQualifier.PRODUCT);
				cartItem.setUnitPrice("std", BigDecimal.ZERO);
				cartItem.setIsImmutable(true);
				cartItem.setTaxCode(TaxCode.REDUCED);
				
				result.put("newCartItem", cartItem);
				
				setStatus(Status.PASSED);
				result.getMessages().add(getPassedMessage());
			}
		} else {
			setStatus(Status.FAILED);
			cart.remove("std-free-pouch");
		}

		return result;
	}

	@Override
	public Message getPassedMessage() {
		return new MessageImpl("coupon", Message.TYPE_INFO, "free-pouch-passed", Arrays.asList(Message.TAG_BOX),
				Arrays.asList());
	}

}
