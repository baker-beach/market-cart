package com.bakerbeach.market.cart.coupon;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class SimpleCouponDao {
	protected static final Logger LOG = Logger.getLogger(SimpleCouponDao.class.getName());

	private MongoTemplate mongoTemplate;
	private String collectionName;

	public Integer getUseCount(String code, String customerId) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and("code").is(code);
		qb.and("customer_id").is(customerId);

		DBObject dbo = getDBCollection().findOne(qb.get(), new BasicDBObject("_id", false).append("count", true));
		if (dbo != null) {
			return (Integer) dbo.get("count");
		} else {
			return 0;
		}
	}

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

		Integer n = getDBCollection().update(q, o, true, false).getN();
		return n;
	}
	
	public void unsetUse(String code, String customerId, Integer dec, String orderId, Date date) {
		QueryBuilder qb = QueryBuilder.start();
		qb.and("code").is(code);
		qb.and("customer_id").is(customerId);
		DBObject q = qb.get();
	
		BasicDBObject o = new BasicDBObject("$inc", new BasicDBObject("count", -1 * dec));
		o.append("$push", new BasicDBObject("booking", new BasicDBObject("order_id", orderId).append("dec", dec).append("date", date)));

		getDBCollection().update(q, o, false, false);
	}
	
	protected DBCollection getDBCollection() {
		return mongoTemplate.getCollection(collectionName);
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
}
