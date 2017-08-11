package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.bakerbeach.market.cart.api.model.CartRuleSetResult;
import com.bakerbeach.market.cart.api.model.RuleMessage;
import com.bakerbeach.market.cart.api.model.RuleResult;

public class SimpleCartRuleSetResult implements CartRuleSetResult {
	private List<RuleResult> results;

	@Override
	public BigDecimal getTotal() {
		BigDecimal total = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(results)) {
			for (RuleResult result : results) {
				if (result.getValues().containsKey("total")) {
					total = total.add(result.getValues().get("total"));
				}
			}
		}

		return total;
	}
	
	@Override
	public List<RuleMessage> getMessages() {
		List<RuleMessage> ruleMessages = new ArrayList<>();		
		if (CollectionUtils.isNotEmpty(results)) {
			for (RuleResult result : results) {
				RuleMessage message = result.getMessage();
				if (message != null) {
					ruleMessages.add(message);
				}
			}
		}

		return ruleMessages;
	}
	
	@Override
	public List<RuleResult> getResults() {
		return results;
	}
	
	public void setResults(List<RuleResult> results) {
		this.results = results;
	}
	
}
