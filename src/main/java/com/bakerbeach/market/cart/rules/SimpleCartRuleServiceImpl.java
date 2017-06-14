package com.bakerbeach.market.cart.rules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.api.model.CartRule;
import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.cart.api.service.CartRuleService;
import com.bakerbeach.market.cart.api.service.CartRulesAware;
import com.bakerbeach.market.core.api.model.Cart;

public class SimpleCartRuleServiceImpl implements CartRuleService {
	protected static final Logger log = LoggerFactory.getLogger(SimpleCartRuleServiceImpl.class);
	
	private Map<String, CartRule> couponRules = new LinkedHashMap<>();
	private Map<String, CartRule> commonRules = new LinkedHashMap<>();
	
	@Override
	public CartRuleContext getNewCartRuleContext() {
		return new SimpleCartRuleContextImpl();
	}

	@Override
	public List<CartRuleResult> lineChangeHandler(CartRuleContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CartRuleResult> lineDiscountHandler(CartRuleContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CartRuleResult> cartDiscountHandler(CartRuleContext context) {
		List<CartRuleResult> results = new ArrayList<>();
		
		Cart cart = context.getCart();
		
		if (cart instanceof CartRulesAware) {
			Set<String> activeCommonRules = context.getCartRuleAware().getCommonRules();
			Set<String> activeCouponRules = context.getCartRuleAware().getCouponRules();
			
			// check the active common rules ---
			for (Entry<String, CartRule> e : commonRules.entrySet()) {
				if (!activeCommonRules.contains(e.getKey())) {
					activeCommonRules.add(e.getKey());
				}
			}
		
			// apply common rules ---
			for (String key : activeCommonRules) {
				CartRuleResult result = commonRules.get(key).apply(context);
				results.add(result);
			}
			
			// apply coupon rules ----
			for (String key : activeCouponRules) {
				CartRuleResult result = couponRules.get(key).apply(context);
				results.add(result);
			}
		
		}

		return results;
	}
	
	public void setCommonRules(Map<String, CartRule> commonRules) {
		this.commonRules = commonRules;
	}
	
	public void setCouponRules(Map<String, CartRule> couponRules) {
		this.couponRules = couponRules;
	}

}
