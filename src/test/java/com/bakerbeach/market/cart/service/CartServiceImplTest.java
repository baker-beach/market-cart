package com.bakerbeach.market.cart.service;
import java.math.BigDecimal;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bakerbeach.market.cart.api.service.CartService;
import com.bakerbeach.market.cart.api.service.CartServiceException;
import com.bakerbeach.market.cart.model.CartItemImpl;
import com.bakerbeach.market.cart.model.SimpleCartImpl;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.customer.model.CustomerImpl;

import junit.framework.Assert;

@ActiveProfiles(profiles = { "env.test", "product.published" })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
public class CartServiceImplTest {

	@Autowired
	private CartService cartService;

	private static Customer customer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CustomerImpl customer = new CustomerImpl();
		customer.setId("1");
		customer.setEmail("foo@bar.de");

		CartServiceImplTest.customer = customer;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void saveCart() {
		try {
			// create a new cart ----
			final Cart cart = new SimpleCartImpl();
			cart.setCustomerId(customer.getId());
			cart.setUpdatedBy(customer.getId());

			CartItem item = new CartItemImpl("gtin-001", BigDecimal.ONE);
			item.setQuantity(new BigDecimal("3"));
			item.setUnitPrice(new BigDecimal("21.11"));
			item.setTotalPrice(new BigDecimal("42.22"));

			cart.add(item);

			// save the cart ---
			cartService.saveCart(customer, cart);
						
			// load by customer ---
			List<Cart> carts = cartService.loadCart(null, customer, null, null);
			Cart cart1 = carts.get(0);			
			Assert.assertTrue("cart is null - not found", cart1 != null);
			
			// load same cart by id ---
			Cart cart2 = cartService.loadCart(null, cart.getId());
			Assert.assertTrue(cart2 != null);
			Assert.assertTrue(cart1.equals(cart2));
			
			// update cart status and save ---
			cart1.setStatus("CLOSED");
			cartService.saveCart(customer, cart1);
			
			// try to save cart2 - should fail ---
			try {
				cartService.saveCart(customer, cart2);				
			} catch (CartServiceException e) {
				Assert.assertTrue(true);
			}
			
			cartService.deleteCart(cart);			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
