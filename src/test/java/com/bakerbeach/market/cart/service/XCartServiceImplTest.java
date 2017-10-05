package com.bakerbeach.market.cart.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.bakerbeach.market.cart.model.XCartImpl;
import com.bakerbeach.market.core.api.model.Address;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.Currency;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.core.api.model.FilterList;
import com.bakerbeach.market.core.api.model.ShopContext;
import com.bakerbeach.market.customer.model.CustomerImpl;

import junit.framework.Assert;

@ActiveProfiles(profiles = { "env.test", "product.published" })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml" })
public class XCartServiceImplTest {

	@Autowired
	private CartService xCartService;

	private static Customer customer;
	private static ShopContext context;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CustomerImpl customer = new CustomerImpl();
		customer.setId("1");
		customer.setEmail("foo@bar.de");

		XCartServiceImplTest.customer = customer;
		
		XCartServiceImplTest.context = new ShopContext() {
			
			@Override
			public void setValidSteps(Set<Integer> validSteps) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setValidCountries(List<String> validCountries) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setSolrUrl(String solrUrl) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setShopType(String type) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setShopCode(String shopCode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setShippingAddress(Address shippingAddress) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setSecurePort(Integer securePort) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setRemoteIp(String remoteIp) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setProtocol(String protocol) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setPriceGroups(List<String> priceGroups) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setPort(Integer port) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setPath(String path) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setPageId(String pageId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setOrderStatus(String orderStatus) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setOrderSequenceRandomOffset(Long orderSequenceRandomOffset) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setOrderSequenceCode(String orderSequenceCode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setOrderId(String orderId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setLocales(List<Locale> locales) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setHost(String host) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setGtmId(String gtmId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setGroupCodesString(String groupCodesString) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setGroupCodes(List<String> groupCodes) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFilterList(FilterList filterList) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDeviceClass(String deviceClass) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDevice(String device) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDefaultPriceGroup(String defaultPriceGroup) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDefaultLocale(Locale defaultLocale) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDefaultCountryOfDelivery(String defaultCountryOfDelivery) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCurrentPriceGroup(String currentPriceGroup) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCurrentLocale(Locale currentLocale) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCurrency(String currency) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCartCode(String cartCode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setBillingAddress(Address billingAddress) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setAssortmentCode(String assortmentCode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public ShopContext refine(Customer customer) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isCountryValid(String countryCode) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Set<Integer> getValidSteps() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<String> getValidCountries() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getSolrUrl() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getShopType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getShopCode() {
				return "TEST_SHOP";
			}
			
			@Override
			public Address getShippingAddress() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Integer getSecurePort() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRightCurrencySymbol() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, Object> getRequestData() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getRemoteIp() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getProtocol() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<String> getPriceGroups() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Integer getPort() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPageId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getOrderStatus() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Long getOrderSequenceRandomOffset() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getOrderSequenceCode() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getOrderId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<String> getNewsletterIds() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<Locale> getLocales() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getLeftCurrencySymbol() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getHost() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getGtmId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<String> getGroupCodes() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public FilterList getFilterList() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getDeviceClass() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getDevice() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getDefaultPriceGroup() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Locale getDefaultLocale() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getDefaultCurrency() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getDefaultCountryOfDelivery() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getCurrentPriceGroup() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Locale getCurrentLocale() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Currency getCurrentCurrency() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Map<String, Currency> getCurrencies() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getCountryOfDelivery() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getCartCode() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Address getBillingAddress() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getAssortmentCode() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public HttpServletRequest getHttpServletRequest() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public HttpServletResponse getHttpServletResponse() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getApplicationPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
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
			final Cart cart = new XCartImpl("TEST_SHOP");
			cart.setCustomerId(customer.getId());
			cart.setUpdatedBy(customer.getId());

			CartItem item = cart.getNewItem("gtin-001", BigDecimal.ONE);
			item.setQuantity(new BigDecimal("3"));
			item.setUnitPrice("std", new BigDecimal("3.42"));
			item.setId(item.createId());

			cart.add(item);

			// save the cart ---
			xCartService.saveCart(customer, cart);
						
			// load by customer ---
			List<Cart> carts = xCartService.loadCart(context, customer, null, null);
			Cart cart1 = carts.get(0);			
			Assert.assertTrue("cart is null - not found", cart1 != null);
			
			// load same cart by id ---
			Cart cart2 = xCartService.loadCart(context, cart1.getId());
			Assert.assertTrue(cart2 != null);
			Assert.assertTrue(cart1.equals(cart2));
			
			// update cart status and save ---
			cart1.setStatus("CLOSED");
			xCartService.saveCart(customer, cart1);
			
			// try to save cart2 - should fail ---
			try {
				xCartService.saveCart(customer, cart2);				
			} catch (CartServiceException e) {
				Assert.assertTrue(true);
			}
			
			xCartService.deleteCart(cart);			
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
