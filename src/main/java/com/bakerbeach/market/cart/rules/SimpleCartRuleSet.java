package com.bakerbeach.market.cart.rules;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bakerbeach.market.cart.api.model.CartRule;
import com.bakerbeach.market.cart.api.model.CartRuleSet;

public class SimpleCartRuleSet implements CartRuleSet, Serializable {
	private Map<String, CartRule> commonRules = new LinkedHashMap<>();
	private Map<String, CartRule> codeRules = new LinkedHashMap<>();
	// private Map<String, CartRule> rules = new LinkedHashMap<>();

	private Date updatedAt = new Date(0);

	@Override
	public Set<Entry<String, CartRule>> entrySet() {
		return getAll().entrySet();
	}

	@Override
	public CartRule get(Object key) {
		return commonRules.containsKey(key) ? commonRules.get(key) : codeRules.get(key);
	}

	@Override
	public Map<String, CartRule> getAll() {
		Map<String, CartRule> map = new LinkedHashMap<>(commonRules);
		map.putAll(codeRules);
		return map;
	}

	@Override
	public void addCodeRule(String key, CartRule rule) {
		codeRules.put(key, rule);
	}

	@Override
	public void addCommonRule(String key, CartRule rule) {
		commonRules.put(key, rule);
	}

	@Override
	public boolean containsKey(Object key) {
		return commonRules.containsKey(key) ? true : codeRules.containsKey(key);
	}

	@Override
	public void clearCodeRules() {
		codeRules.clear();
	}

	@Override
	public Map<String, CartRule> getCodeRules() {
		return codeRules;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

}
