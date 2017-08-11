package com.bakerbeach.market.cart.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bakerbeach.market.cart.api.model.CartRule;
import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleSet;
import com.bakerbeach.market.cart.api.model.CartRuleSetResult;
import com.bakerbeach.market.cart.api.model.CartRuleStore;
import com.bakerbeach.market.cart.api.model.RuleResult;

public class SimpleCartRuleSet implements CartRuleSet {
	private Map<String, CartRule> rules = new LinkedHashMap<>();

	private Date updatedAt = new Date(0);

	@Override
	public void init(CartRuleStore store) {

		// TODO: check cache time

		for (String id : store.getCommonRuleIds()) {
			if (!rules.containsKey(id)) {
				CartRule instance = store.getInstance(id);
				rules.put(instance.getId(), instance);
			}
		}

	}
	
	@Override
	public CartRuleSetResult apply(CartRule.Intention intention, CartRuleContext context) {
		List<RuleResult> results = new ArrayList<>();
		for (CartRule rule : rules.values()) {
			RuleResult result = rule.apply(intention, context);
			results.add(result);
		}
		
		SimpleCartRuleSetResult ruleSetResult = new SimpleCartRuleSetResult();
		ruleSetResult.setResults(results);

		return ruleSetResult;
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
	public CartRule add(CartRule rule) {
		return rules.put(rule.getId(), rule);
	}

	@Override
	public void addAll(Collection<CartRule> rules) {
		for (CartRule rule : rules) {
			add(rule);
		}
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

}
