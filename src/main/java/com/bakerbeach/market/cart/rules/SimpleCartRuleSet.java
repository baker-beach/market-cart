package com.bakerbeach.market.cart.rules;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bakerbeach.market.cart.api.model.CartRule;
import com.bakerbeach.market.cart.api.model.CartRuleSet;

public class SimpleCartRuleSet implements CartRuleSet {
	private Map<String, CartRule> rules = new LinkedHashMap<>();

	private Date updatedAt = new Date(0);

	@Override
	public Set<Entry<String, CartRule>> entrySet() {
		return rules.entrySet();
	}

	@Override
	public CartRule get(Object key) {
		return rules.get(key);
	}

	@Override
	public Map<String, CartRule> getAll() {
		return rules;
	}

	@Override
	public CartRule put(String key, CartRule rule) {
		return rules.put(key, rule);
	}
	
	@Override
	public boolean containsKey(Object key) {
		return rules.containsKey(key);
	}

//	@Override
//	public CartRule add(CartRule rule) {
//		return rules.put(rule.getId(), rule);
//	}

//	@Override
//	public void addAll(Collection<CartRule> rules) {
//		for (CartRule rule : rules) {
//			add(rule);
//		}
//	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

}
