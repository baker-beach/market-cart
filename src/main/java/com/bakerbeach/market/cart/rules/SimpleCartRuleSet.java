package com.bakerbeach.market.cart.rules;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bakerbeach.market.cart.api.model.CartRuleSet;
import com.bakerbeach.market.cart.api.model.RuleInstance;

public class SimpleCartRuleSet implements CartRuleSet {
	private Map<String, RuleInstance> commonRules = new LinkedHashMap<>();
	private Map<String, RuleInstance> codeRules = new LinkedHashMap<>();

	private Date updatedAt = new Date(0);

	@Override
	public Set<Entry<String, RuleInstance>> entrySet() {
		return getAll().entrySet();
	}

	@Override
	public RuleInstance get(Object key) {
		return commonRules.containsKey(key) ? commonRules.get(key) : codeRules.get(key);
	}

	@Override
	public Map<String, RuleInstance> getAll() {
		Map<String, RuleInstance> map = new LinkedHashMap<>(commonRules);
		map.putAll(codeRules);
		return map;
	}

	@Override
	public void addCodeRule(String key, RuleInstance rule) {
		codeRules.put(key, rule);
	}

	@Override
	public void addCommonRule(String key, RuleInstance rule) {
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
	public Map<String, RuleInstance> getCodeRules() {
		return codeRules;
	}

	@Override
	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	@Override
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
