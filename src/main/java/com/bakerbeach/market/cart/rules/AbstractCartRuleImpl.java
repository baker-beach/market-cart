package com.bakerbeach.market.cart.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.api.model.CartRule;

public abstract class AbstractCartRuleImpl extends AbstractRuleImpl implements CartRule {
	protected static final Logger log = LoggerFactory.getLogger(AbstractCartRuleImpl.class);
	
	protected Intention intention;
	protected Set<String> codes = new HashSet<>();
	protected Integer maxIndividualUse;
	protected Set<String> emails = new HashSet<String>();
	protected Boolean newsletterSubscription = false;
	protected String customerId;
	protected Integer useCount;
	Boolean isUsed = false;
	
	@Override
	public CartRule getInstance() {
		CartRule instance = (CartRule) super.getInstance();
		instance.setIntention(intention);
		instance.setMaxIndividualUse(maxIndividualUse);
		instance.setEmails(emails);
		instance.setNewsletterSubscription(newsletterSubscription);

		return instance;
	}
	
	@Override
	public Intention getIntention() {
		return intention;
	}

	@Override
	public void setIntention(Intention intention) {
		this.intention = intention;
	}
	
	@Override
	public Set<String> getCodes() {
		return codes;
	}
	
	public void setCodes(Set<String> codes) {
		this.codes = codes;
	}
	
	public void setCodesStr(String codesStr) {
		codes.addAll(Arrays.asList(codesStr.split(",")));		
	}
	
	@Override
	public Integer getMaxIndividualUse() {
		return maxIndividualUse;
	}

	@Override
	public void setMaxIndividualUse(Integer maxIndividualUse) {
		this.maxIndividualUse = maxIndividualUse;
	}

	@Override
	public Set<String> getEmails() {
		return emails;
	}

	@Override
	public void setEmails(Set<String> emails) {
		this.emails = emails;
	}

	@Override
	public Boolean getNewsletterSubscription() {
		return newsletterSubscription;
	}

	@Override
	public void setNewsletterSubscription(Boolean newsletterSubscription) {
		this.newsletterSubscription = newsletterSubscription;
	}
	
	@Override
	public String getCustomerId() {
		return customerId;
	}
	
	@Override
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@Override
	public Integer getUseCount() {
		return useCount;
	}
	
	@Override
	public void setUseCount(Integer useCount) {
		this.useCount = useCount;
	}
	
	@Override
	public Boolean getIsUsed() {
		return isUsed;
	}
	
	@Override
	public void setIsUsed(boolean isUsed) {
		this.isUsed = isUsed;		
	}
	
}
