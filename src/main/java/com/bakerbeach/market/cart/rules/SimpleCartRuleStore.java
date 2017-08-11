package com.bakerbeach.market.cart.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.bakerbeach.market.cart.api.model.CartRule;
import com.bakerbeach.market.cart.api.model.CartRuleStore;
import com.bakerbeach.market.cart.api.model.RuleStore;

public class SimpleCartRuleStore implements CartRuleStore {
	private RuleStore ruleStore;
	private Map<String, CartRule> codeRules = new LinkedHashMap<>();
	private Map<String, CartRule> commonRules = new LinkedHashMap<>();

	@Override
	public Set<String> getCommonRuleIds() {
		return commonRules.keySet();
	}
	
	@Override
	public CartRule getCodeRuleInstance(String code) {
		if( codeRules.containsKey(code)) {
			CartRule instance = (CartRule) codeRules.get(code).getInstance();
			return instance;
		} else {
			return null;
		}
	}

	@Override
	public CartRule getInstance(String id) {
		return (CartRule) ruleStore.getInstance(id);
	}
	
	@Override
	public Collection<CartRule> getCommonRuleInstances() {
		Collection<CartRule> instances = new ArrayList<>();
		for (CartRule rule : commonRules.values()) {
			CartRule instance = (CartRule) rule.getInstance();
			instances.add(instance);
		}		
		return instances;
	}

	public void setRules(Collection<CartRule> rules) {
		for (CartRule rule : rules) {
			ruleStore.setRule(rule);

			Set<String> codes = rule.getCodes();
			if (CollectionUtils.isNotEmpty(codes)) {
				for (String code : codes) {
					codeRules.put(code, rule);
				}
			} else {
				commonRules.put(rule.getId(), rule);
			}
		}
	}

	public void setRuleStore(RuleStore ruleStore) {
		this.ruleStore = ruleStore;
		// init();
	}

}
