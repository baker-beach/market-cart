package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bakerbeach.market.cart.api.model.RuleMessage;
import com.bakerbeach.market.cart.api.model.RuleResult;

public class SimpleCartRuleResult implements RuleResult {
	private Map<String, Object> map = new HashMap<>();
	private Map<String, BigDecimal> values = new HashMap<String, BigDecimal>();
	private RuleMessage message;

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	@Override
	public Map<String, BigDecimal> getValues() {
		return values;
	}

	@Override
	public RuleMessage getMessage() {
		return message;
	}

	@Override
	public void setMessage(RuleMessage message) {
		this.message = message;
	}

	@Override
	public boolean hasError() {
		return message.getType().equals(RuleMessage.Type.ERROR);
	}

}
