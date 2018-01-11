package com.bakerbeach.market.cart.rules;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.annotations.Id;

import com.bakerbeach.market.cart.api.model.RuleTmpl;

public class RuleTmplImpl implements RuleTmpl {

	@Id
	private String id;
	private Type type = Type.CODE;
	private List<Intention> intentions;
	private Date start;
	private Date end;
	private Integer maxIndividualUse;
	private Map<String, Object> data = new LinkedHashMap<>();
	private String clazz;
	protected Set<String> emails = new HashSet<String>();
	protected Boolean newsletterSubscription = false;

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
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
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
	public Integer getMaxIndividualUse() {
		return maxIndividualUse;
	}

	@Override
	public void setMaxIndividualUse(Integer maxIndividualUse) {
		this.maxIndividualUse = maxIndividualUse;
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
	public String getClazz() {
		return clazz;
	}

	@Override
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public Set<String> getEmails() {
		return emails;
	}
	
	@Override
	public void setEmails(Set<String> emails) {
		setEmails(emails);
	}
	
	@Override
	public Boolean getNewsletterSubscription() {
		return newsletterSubscription;
	}
	
	@Override
	public void setNewsletterSubscription(Boolean newsletterSubscription) {
		this.newsletterSubscription = newsletterSubscription;
	}
	
}
