package com.bakerbeach.market.cart.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bakerbeach.market.cart.api.model.CartRuleSet;
import com.bakerbeach.market.cart.api.model.CartRuleStore;
import com.bakerbeach.market.cart.api.service.CartRuleAware;
import com.bakerbeach.market.cart.api.service.CartService;
import com.bakerbeach.market.cart.api.service.CartServiceException;
import com.bakerbeach.market.commons.Message;
import com.bakerbeach.market.commons.Messages;
import com.bakerbeach.market.commons.MessagesImpl;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.core.api.model.ShopContext;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.customer.model.CustomerImpl;

@ActiveProfiles(profiles = { "env.test", "product.published" })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
public class CartRuleTest {

	@Autowired
	public CartRuleStore cartRuleStore; 

	@Autowired
	private CartService xCartService;

	private static Customer customer;
	private static ShopContext context;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CustomerImpl customer = new CustomerImpl();
		customer.setId("1");
		customer.setEmail("foo@bar.de");
		customer.setTaxCode(TaxCode.NORMAL);
		CartRuleTest.customer = customer;
		
		CartRuleTest.context = new TestShopContext();
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
	public void checkInstanceStatus() {
		
		try {
			// load by customer ---
			List<Cart> carts = xCartService.loadCart(context, customer, null, null);
			Cart cart = carts.get(0);			
			Assert.assertTrue(cart != null);			

			CartRuleSet cartRuleSet = null;
			if (cart instanceof CartRuleAware) {
				cartRuleSet = ((CartRuleAware) cart).getCartRuleSet();
				cartRuleSet.init(cartRuleStore);				
			}
			
			{
				CartItem item = cart.getNewItem("gtin-001", BigDecimal.ONE);
				item.setQuantity(new BigDecimal("6"));
				item.setUnitPrice("std", new BigDecimal("3.42"));
				item.setQualifier("PRODUCT");
				item.setTaxCode(TaxCode.REDUCED);
				item.setId(item.createId());
				cart.add(item);				
			}
			
			{
				CartItem item = cart.getNewItem("gtin-002", BigDecimal.ONE);
				item.setQuantity(new BigDecimal("5"));
				item.setUnitPrice("std", new BigDecimal("3.42"));
				item.setQualifier("PRODUCT");
				item.setTaxCode(TaxCode.REDUCED);
				item.setId(item.createId());
				cart.add(item);				
			}
			
			xCartService.calculate(CartRuleTest.context, cart, CartRuleTest.customer);
			
			Messages messages = new MessagesImpl();
			xCartService.calculate(CartRuleTest.context, cart, CartRuleTest.customer, messages);
			
			for (CartItem item : cart.getItems().values()) {
				System.out.println(String.format("item: %s, %s", item.getId(), item.getTotalPrice("std")));
			}
			System.out.println(String.format("total: %s", cart.getTotal().getGross()));
			System.out.println(String.format("discount: %s", cart.getDiscount().getGross()));
			System.out.println(String.format("shipping: %s", cart.getShipping()));

			try {
				xCartService.setRuleUse(CartRuleTest.context, cart, CartRuleTest.customer, "4711");
				// xCartService.setIndividualUse(coupon, customerId, orderId, cart, shopCode);;
			} catch (CartServiceException e) {
				xCartService.unsetRuleUse(CartRuleTest.context, cart, CartRuleTest.customer, "4711");
				// Assert.assertTrue(e.getMessage(), false);
			}

			for (Message message : messages) {
				System.out.println(message);
				//System.out.println(String.format("message: %s, %s, %s", message.getType(), message.getCode(), Arrays.asList(message.getArgs())));
			}

		} catch (Exception e) {
			Assert.assertTrue(false);
		}
		
	}


}
