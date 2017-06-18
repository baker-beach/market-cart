package com.bakerbeach.market.cart.rules;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.api.model.CartRule;
import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;

public class SimpleCartRuleImpl implements CartRule {
	protected static final Logger log = LoggerFactory.getLogger(SimpleCartRuleImpl.class);

	private String code;
	private BigDecimal rate;
	private BigDecimal shippingDiscountRate;
	private Integer maxIndividualUse;
	private Set<String> emails = new HashSet<String>();
	private Boolean newsletterSubscription = false;
	private Date start;
	private Date end;

	@Override
	public CartRule getInstance() {
		SimpleCartRuleImpl coupon = new SimpleCartRuleImpl();
		coupon.setCode(this.getCode());
		coupon.setRate(this.getRate());
		coupon.setShippingDiscountRate(this.getShippingDiscountRate());
		coupon.setMaxIndividualUse(maxIndividualUse);
		coupon.setEmails(this.getEmails());
		coupon.setNewsletterSubscription(this.getNewsletterSubscription());
		coupon.setStart(this.getStart());
		coupon.setEnd(this.getEnd());

		return coupon;
	}

	@Override
	public CartRuleResult apply(CartRuleContext context) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	@Override
	public BigDecimal getShippingDiscountRate() {
		return shippingDiscountRate;
	}

	public void setShippingDiscountRate(BigDecimal shippingDiscountRate) {
		this.shippingDiscountRate = shippingDiscountRate;
	}

	@Override
	public Integer getMaxIndividualUse() {
		return maxIndividualUse;
	}

	public void setMaxIndividualUse(Integer maxIndividualUse) {
		this.maxIndividualUse = maxIndividualUse;
	}

	@Override
	public Set<String> getEmails() {
		return emails;
	}

	public void setEmails(Set<String> emails) {
		this.emails = emails;
	}

	@Override
	public Boolean getNewsletterSubscription() {
		return newsletterSubscription;
	}

	public void setNewsletterSubscription(Boolean newsletterSubscription) {
		this.newsletterSubscription = newsletterSubscription;
	}

	@Override
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	@Override
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

}
