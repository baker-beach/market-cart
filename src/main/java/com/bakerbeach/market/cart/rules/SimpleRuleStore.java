package com.bakerbeach.market.cart.rules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bakerbeach.market.cart.api.model.Rule;
import com.bakerbeach.market.cart.api.model.RuleStore;

public class SimpleRuleStore implements RuleStore {

	private Map<String, Rule> rules = new HashMap<>();

	@Override
	public Rule getInstance(String id) {
		Rule rule = getRule(id);
		if (rule != null) {
			return rule.getInstance();
		} else {
			return null;
		}
	}
	
	@Override
	public Set<String> getIds() {
		return rules.keySet();
	}

	@Override
	public Collection<Rule> getRules() {
		return rules.values();
	}

	@Override
	public Rule getRule(String id) {
		return rules.get(id);
	}

	@Override
	public void setRule(Rule rule) {
		this.rules.put(rule.getId(), rule);		
	}
	
	@Override
	public void setRules(Collection<Rule> rules) {
		for (Rule rule : rules) {
			this.rules.put(rule.getId(), rule);
		}
	}

}