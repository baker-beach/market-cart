package com.bakerbeach.market.cart.dao;

import java.util.List;

import com.bakerbeach.market.cart.api.service.CartServiceException;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.Customer;

public interface MongoCartDao {

	Cart getNewCart() throws CartServiceException;

	void saveCart(Cart cart) throws CartServiceException;

	Cart loadCart(String id) throws CartServiceException;

	List<Cart> loadCart(Customer customer, List<String> shopCode, List<String> status) throws CartServiceException;

	void deleteCart(Cart cart);

}
