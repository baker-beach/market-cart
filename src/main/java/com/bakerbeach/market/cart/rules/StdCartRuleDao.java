package com.bakerbeach.market.cart.rules;

import java.util.Date;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class StdCartRuleDao implements CartRuleDao {
	protected static final Logger log = LoggerFactory.getLogger(StdCartRuleDao.class);

	protected static final String DEFAULT_RULE_USE_COLLECTION_NAME = "rule_use";

	protected Morphia morphia = new Morphia();
	protected Datastore datastore;
	protected String ruleUseCollectionName = DEFAULT_RULE_USE_COLLECTION_NAME;

	public StdCartRuleDao(Datastore datastore) {
		this.datastore = datastore;
	}

	@Override
	public Integer getUseCount(String code, String customerId) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and("code").is(code);
		qb.and("customer_id").is(customerId);

		DBObject dbo = datastore.getDB().getCollection(ruleUseCollectionName).findOne(qb.get(),
				new BasicDBObject("_id", false).append("count", true));
		if (dbo != null) {
			return (Integer) dbo.get("count");
		} else {
			return 0;
		}
	}
	
	@Override
	public Integer setUse(String code, String customerId, Integer inc, Integer max, String orderId, Date date) {		
		QueryBuilder qb = QueryBuilder.start();
		qb.and("code").is(code);
		qb.and("customer_id").is(customerId);
		if (max != null) {
			qb.and("count").lessThanEquals(max - inc);
		}
		DBObject q = qb.get();
	
		BasicDBObject o = new BasicDBObject("$inc", new BasicDBObject("count", inc));
		o.append("$push", new BasicDBObject("booking", new BasicDBObject("order_id", orderId).append("inc", inc).append("date", date)));
		o.append("$setOnInsert", new BasicDBObject("code", code).append("customer_id", customerId));

		Integer n = datastore.getDB().getCollection(ruleUseCollectionName).update(q, o, true, false).getN();
		return n;
	}
	
	@Override
	public void unsetUse(String code, String customerId, Integer dec, String orderId, Date date) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and("code").is(code);
		qb.and("customer_id").is(customerId);
		DBObject q = qb.get();
	
		BasicDBObject o = new BasicDBObject("$inc", new BasicDBObject("count", -1 * dec));
		o.append("$push", new BasicDBObject("booking", new BasicDBObject("order_id", orderId).append("dec", dec).append("date", date)));

		datastore.getDB().getCollection(ruleUseCollectionName).update(q, o, false, false);
	}

}
