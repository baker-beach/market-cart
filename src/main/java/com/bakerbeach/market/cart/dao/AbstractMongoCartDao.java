package com.bakerbeach.market.cart.dao;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bakerbeach.market.cart.api.service.CartServiceException;
import com.bakerbeach.market.cart.model.SimpleCartImpl;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.Customer;
import com.mongodb.DBCollection;

public abstract class AbstractMongoCartDao<T extends Cart> implements MongoCartDao {
	protected static final Logger log = LoggerFactory.getLogger(AbstractMongoCartDao.class);

	protected static final String DEFAULT_CART_COLLECTION_NAME = "cart";

	protected Morphia morphia = new Morphia();
	protected Datastore datastore;
	protected String cartCollectionName = DEFAULT_CART_COLLECTION_NAME;
	protected Class<T> cartClass;

	public AbstractMongoCartDao(Class<T> cartClass, Datastore datastore) {
		this.cartClass = cartClass;
		this.datastore = datastore;
	}

	@Override
	public Cart getNewCart() throws CartServiceException {
		try {
			return cartClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new CartServiceException();
		}
	}

	@Override
	public Cart loadCart(String id) throws CartServiceException {
		try {
			final Query<T> query = ((AdvancedDatastore) datastore).createQuery(cartCollectionName, cartClass)
					.field("_id").equal(new ObjectId(id));
			return query.get();
		} catch (Exception e) {
			throw new CartServiceException();
		}
	}

	@Override
	public List<Cart> loadCart(Customer customer, List<String> shopCode, List<String> status)
			throws CartServiceException {
		try {
			final Query<T> query = ((AdvancedDatastore) datastore).createQuery(cartCollectionName, cartClass)
					.field("customer_id").equal(customer.getId());
			if (CollectionUtils.isNotEmpty(shopCode)) {
				query.field("shopCode").in(shopCode);
			}
			if (CollectionUtils.isNotEmpty(status)) {
				query.field("status").in(status);
			}
			query.order("-updated_at");

			List<T> carts = query.asList();

			return new ArrayList<Cart>(carts);
		} catch (Exception e) {
			throw new CartServiceException();
		}
	}

	public void saveCart(Cart cart) throws CartServiceException {
		try {
			((AdvancedDatastore) datastore).save(cartCollectionName, cart);
		} catch (ConcurrentModificationException e) {
			throw new CartServiceException();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			throw new CartServiceException();
		}
	}

	@Override
	public void deleteCart(Cart cart) {
		((AdvancedDatastore) datastore).delete(cartCollectionName, cartClass, new ObjectId(cart.getId()));
	}

}
