package com.bakerbeach.market.cart.rules;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;

import com.bakerbeach.market.cart.api.model.CartRuleContext;

public class SimpleCartRuleContextImpl implements CartRuleContext {
	private Map<String, Object> map = new HashedMap<>();

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
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

}
