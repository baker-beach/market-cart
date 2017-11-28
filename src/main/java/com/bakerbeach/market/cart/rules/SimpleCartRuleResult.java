package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.commons.Messages;
import com.bakerbeach.market.commons.MessagesImpl;

public class SimpleCartRuleResult implements CartRuleResult {
	private String id;
	private String code = "default";
	private Map<String, Object> map = new HashMap<>();
	private Map<String, BigDecimal> values = new HashMap<String, BigDecimal>();
	private Messages messages = new MessagesImpl();

	public SimpleCartRuleResult(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getCode() {
		return code;
	}
	
	@Override
	public void setCode(String code) {
		this.code = code;
	}
		
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
	public Messages getMessages() {
		return messages;
	}
	
}
