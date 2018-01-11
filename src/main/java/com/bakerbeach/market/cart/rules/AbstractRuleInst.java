package com.bakerbeach.market.cart.rules;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.api.model.RuleInstance;
import com.bakerbeach.market.commons.Message;

public abstract class AbstractRuleInst implements RuleInstance {
	protected static final Logger log = LoggerFactory.getLogger(AbstractRuleInst.class);

	protected String id;
	protected Type type;
	protected Date start = new GregorianCalendar(2017, 0, 1).getTime();
	protected Date end = new GregorianCalendar(2021, 0, 1).getTime();
	private Map<String, Object> data = new LinkedHashMap<>();
	protected List<Intention> intentions;
	protected Status status = Status.ENABLED;
	protected Set<String> codes = new HashSet<>();
	protected Integer maxIndividualUse;
	protected Set<String> emails = new HashSet<String>();
	protected Boolean newsletterSubscription = false;
	protected String customerId;
	protected Integer useCount;
	Boolean isUsed = false;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setType(Type type) {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getStart() {
		return start;
	}

	@Override
	public void setStart(Date start) {
		this.start = start;
	}

	@Override
	public Date getEnd() {
		return end;
	}

	@Override
	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public List<Intention> getIntentions() {
		return intentions;
	}

	@Override
	public void setIntentions(List<Intention> intentions) {
		this.intentions = intentions;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setStatus(Status status) {
		this.status = status;
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
	public Map<String, Object> getData() {
		return data;
	}

	@Override
	public void setData(Map<String, Object> data) {
		this.data = data;
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
	
	@Override
	public Message getDisabledMessage() {
		return null;
	}
	
	@Override
	public Message getFailedMessage() {
		return null;
	}

	@Override
	public Message getPassedMessage() {
		return null;
	}

}
