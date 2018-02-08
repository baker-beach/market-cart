package com.bakerbeach.market.cart.rules;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

@Entity(value = "rule_mapping")
public class RuleMapping {
	@Id
	private String id;
	private String code;
	@Property("rule_id")
	private String ruleId;
	@Property("shop_code")
	private List<String> shopCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public List<String> getShopCode() {
		return shopCode;
	}
	
	public void setShopCode(List<String> shopCode) {
		this.shopCode = shopCode;
	}
	
}
