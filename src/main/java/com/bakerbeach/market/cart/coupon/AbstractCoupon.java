package com.bakerbeach.market.cart.coupon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bakerbeach.market.core.api.model.Coupon;
import com.bakerbeach.market.core.api.model.CouponError;

public abstract class AbstractCoupon implements Serializable, Coupon {
	protected static final Logger LOG = Logger.getLogger(Coupon.class.getName());

	protected String nature = Coupon.NATURE_RULECOUPON;
	protected String code;
	protected Integer ruleId;
	protected Long maxUsesPerCoupon = Long.MAX_VALUE;
	protected Long maxUsesPerCustomer = Long.MAX_VALUE;
	protected String extProvider;
	protected Map legacyRedeemRule;
	protected CouponError error;
	protected boolean codeMatched = false;


	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}
	
	public CouponError getError() {
		return error;
	}

	public void setError(CouponError error) {
	    this.error = error;
	}

	public boolean getCodeMatched() {
		return codeMatched;
	}

	public void setCodeMatched(boolean codeMatched) {
		this.codeMatched = codeMatched;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getMaxUsesPerCoupon() {
		return maxUsesPerCoupon;
	}

	public void setMaxUsesPerCoupon(Long maxUsesPerCoupon) {
		this.maxUsesPerCoupon = maxUsesPerCoupon;
	}

	public Long getMaxUsesPerCustomer() {
		return maxUsesPerCustomer;
	}

	public void setMaxUsesPerCustomer(Long maxUsesPerCustomer) {
		this.maxUsesPerCustomer = maxUsesPerCustomer;
	}

	public Integer getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(Integer ruleId){
		this.ruleId = ruleId;
	}
	
	public String getExtProvider(){
		return extProvider;
	}
	
	public void setExtProvider(String extProvider){
		this.extProvider = extProvider;
	}
	
	public Map getLegacyRedeemRule() {
		return legacyRedeemRule;
	}

	public void setLegacyRedeemRule(Map legacyRedeemRule) {
		this.legacyRedeemRule = legacyRedeemRule;
	}

	@Override
	public String getDiscountType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getCartDiscount() {
		// TODO Auto-generated method stub
		return null;
	}

}
