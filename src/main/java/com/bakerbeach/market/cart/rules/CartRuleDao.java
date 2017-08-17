package com.bakerbeach.market.cart.rules;

import java.util.Date;

public interface CartRuleDao {

	Integer getUseCount(String code, String customerId);

	Integer setUse(String code, String customerId, Integer inc, Integer max, String orderId, Date date);

	void unsetUse(String code, String customerId, Integer dec, String orderId, Date date);

}
