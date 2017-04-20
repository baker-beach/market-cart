package com.bakerbeach.market.cart.dao;

import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.model.XCartImpl;

public class MongoCartDaoImpl extends AbstractMongoCartDao<XCartImpl> {
	protected static final Logger log = LoggerFactory.getLogger(MongoCartDaoImpl.class);

	public MongoCartDaoImpl(Datastore datastore) {
		super(XCartImpl.class, datastore);
	}

}
