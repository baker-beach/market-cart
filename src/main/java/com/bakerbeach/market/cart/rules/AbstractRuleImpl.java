package com.bakerbeach.market.cart.rules;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.api.model.Rule;

public abstract class AbstractRuleImpl implements Rule {
	protected static final Logger log = LoggerFactory.getLogger(AbstractRuleImpl.class);

	private String id;
	private Date start = new GregorianCalendar(2017, 0, 1).getTime();
	private Date end = new GregorianCalendar(2021, 0, 1).getTime();
	
	@Override
	public Rule getInstance() {
		try {
			Rule instance = this.getClass().newInstance();
			instance.setId(id);
			instance.setStart(start);
			instance.setEnd(end);
			
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}
		
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
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
	
}
