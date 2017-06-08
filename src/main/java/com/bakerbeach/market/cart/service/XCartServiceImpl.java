package com.bakerbeach.market.cart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.service.CartRuleService;
import com.bakerbeach.market.cart.api.service.CartService;
import com.bakerbeach.market.cart.api.service.CartServiceException;
import com.bakerbeach.market.cart.dao.MongoCartDao;
import com.bakerbeach.market.cart.model.TotalImpl;
import com.bakerbeach.market.cart.model.TotalImpl.LineImpl;
import com.bakerbeach.market.commons.Message;
import com.bakerbeach.market.commons.MessageImpl;
import com.bakerbeach.market.commons.Messages;
import com.bakerbeach.market.commons.MessagesImpl;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.CartItemQualifier;
import com.bakerbeach.market.core.api.model.Coupon;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.core.api.model.ShopContext;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.core.api.model.Total;
import com.bakerbeach.market.core.api.model.Total.Line;
import com.bakerbeach.market.shipping.api.model.ShippingContext;
import com.bakerbeach.market.shipping.api.model.ShippingInfo;
import com.bakerbeach.market.shipping.api.service.ShippingService;
import com.bakerbeach.market.tax.api.service.TaxService;
import com.bakerbeach.market.translation.api.service.TranslationService;

public class XCartServiceImpl implements CartService {
	protected static final Logger log = LoggerFactory.getLogger(XCartServiceImpl.class);
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	protected Map<String, MongoCartDao> mongoCartDaos;

	@Autowired
	private TaxService taxService;

	@Autowired
	private ShippingService shippingService;

	@Autowired
	protected TranslationService translationService;
	
	@Autowired
	protected CartRuleService cartRuleService;

	@Override
	public Cart getInstance(ShopContext shopContext, Customer customer) throws CartServiceException {
		try {
			if (!(customer.isAnonymousCustomer())) {
				Cart cart = loadActiveCart(shopContext, customer);
				if (cart != null) {
					calculate(shopContext, cart, customer);
					return cart;
				}
			}
		} catch (CartServiceException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		return getNewInstance(shopContext, customer);
	}

	@Override
	public Cart getNewInstance(ShopContext shopContext, Customer customer) throws CartServiceException {
		Cart cart = getNewCart(shopContext);
		cart.setCustomerId(customer.getId());
		cart.setCreatedAt(new Date());
		cart.setCreatedBy(customer.getId());
		cart.setShopCode(shopContext.getShopCode());
		cart.setStatus("ACTIVE");
		return cart;
	}

	@Override
	public Messages addCartItem(ShopContext shopContext, Cart cart, CartItem cartItem) throws CartServiceException {
		try {
			Messages messages = new MessagesImpl();

			// TODO: optionally check inventory
			// TODO: optionally set reservation
			// TODO: check or create unique id for the entry

			addCartItemPrecheck(shopContext, cart, cartItem);

			// TODO: update for same id
			// cartItem.getId()

			cart.add(cartItem);

			messages.add(new MessageImpl(Message.TYPE_INFO, "successfully.added.item", cartItem.getId(), cartItem.getCode(), cartItem.getQuantity()));
			return messages;
		} catch (CartServiceException e) {
			return e.getMessages();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));

			Messages messages = new MessagesImpl();
			messages.add(new MessageImpl(Message.TYPE_ERROR, "cart.error."));
			throw new CartServiceException(messages);
		}
	}

	protected void addCartItemPrecheck(ShopContext shopContext, Cart cart, CartItem newCartItem)
			throws CartServiceException {
	}

	@SuppressWarnings("unused")
	@Override
	public final synchronized Messages setQuantity(Cart cart, String id, BigDecimal quantity)
			throws CartServiceException {
		try {
			Messages messages = new MessagesImpl();

			// TODO: optionally check inventory
			// TODO: optionally set reservation
			if (false)
				throw new CartServiceException();

			CartItem item = cart.findItemById(id);
			if (item != null) {
				if (quantity.compareTo(BigDecimal.ZERO) < 1) {
					cart.remove(item);
				} else {
					item.setQuantity(quantity);
				}
				
				messages.addGlobalMessage(
						new MessageImpl(Message.TYPE_INFO, "successfully.updated.item", item.getId(), item.getCode(), item.getQuantity()));
			}

			return messages;
		} catch (CartServiceException e) {
			return e.getMessages();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));

			Messages messages = new MessagesImpl();
			messages.addGlobalMessage(new MessageImpl(Message.TYPE_ERROR, "cart.error"));
			throw new CartServiceException(messages);
		}
	}

	@Override
	public final synchronized void removeCartItem(Cart cart, CartItem cartItem) {
		cart.remove(cartItem);
	}

	@Override
	public final synchronized void clear(Cart cart) {
		cart.clear();
	}

	@Override
	public final synchronized void calculate(ShopContext shopContext, Cart cart, Customer customer) {
		Map<String, CartItem> synchronizedMap = Collections.synchronizedMap(cart.getItems());

		// remove zero quantity and volatile items and clear line discounts ---
		Collection<String> toBeRemoved = new ArrayList<String>();
		synchronizedMap.forEach((k, i) -> {
			if (i.getQuantity().compareTo(BigDecimal.ZERO) < 1 || i.isVolatile()) {
				toBeRemoved.add(k);
			}
		});
		synchronizedMap.keySet().removeAll(toBeRemoved);

		// reset line discount ---
		synchronizedMap.forEach((k, i) -> {
			i.setDiscount(BigDecimal.ZERO);
		});

		// zeilen berechnen (alle produkte) ---
		synchronizedMap.forEach((k, item) -> {
			calculateItem(item, shopContext.getCountryOfDelivery(), customer.getTaxCode());
		});

		Total goods = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT));

		// TODO: set payment ---
		cart.setPayment(BigDecimal.ZERO);

		// TODO: line-discount ---
		/*
		 * List<Coupon> coupons = cart.getCoupons(); for (Coupon coupon :
		 * coupons) { try { CouponResult couponResult =
		 * coupon.apply(shopContext, customer, cart); if
		 * (!couponResult.hasErrors()) { for (CartItem item :
		 * cart.getCartItems()) { if
		 * (couponResult.getDiscounts().containsKey(item.getId())) { BigDecimal
		 * discount = couponResult.getDiscounts().get(item.getId());
		 * item.setDiscount(discount);
		 * 
		 * // create one discount resource item for each line discount --- if
		 * (!discountResourceItems.containsKey(item.getTaxCode())) {
		 * ResourceCartItemImpl resourceCartItem = new ResourceCartItemImpl();
		 * resourceCartItem.setGtin("DISCOUNT_".concat(item.getTaxCode().name())
		 * ); resourceCartItem.setTaxCode(item.getTaxCode());
		 * resourceCartItem.setTaxPercent(item.getTaxPercent());
		 * 
		 * discountResourceItems.put(item.getTaxCode(), resourceCartItem); }
		 * 
		 * CartItem resourceCartItem =
		 * discountResourceItems.get(item.getTaxCode());
		 * resourceCartItem.setUnitPrice(resourceCartItem.getUnitPrice().add(
		 * item.getDiscount()));
		 * resourceCartItem.setTotalPrice(resourceCartItem.getTotalPrice().add(
		 * item.getDiscount())); } } } } catch (Exception e) { for (CartItem
		 * item : cart.getCartItems()) { item.setDiscount(BigDecimal.ZERO); }
		 * 
		 * log.error(ExceptionUtils.getStackTrace(e)); } }
		 */

		// TODO: waren inkl. discount ---

		Total shippingGoods = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT));

		cart.setValueOfShippingGoods(shippingGoods.getGross());

		// shipmentService.setOptionalCartItems(cart, customer);

		goods = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT));

		// TODO: set shipping cart item

		// lieferkosten ---
		if (!cart.getItems().isEmpty()) {
			try {

				ShippingContext shippingContext = (ShippingContext) shopContext.getSessionData()
						.get(ShippingContext.CONTEXT_KEY);
				if (shippingContext == null) {
					shippingContext = shippingService.createShippingContext(shopContext, customer, cart);
					shopContext.getSessionData().put(ShippingContext.CONTEXT_KEY, shippingContext);
				}

				if (!shippingService.checkShippingContext(shopContext, customer, cart, shippingContext)) {
					ShippingInfo shippingInfo = shippingService.apply(shippingContext);

					CartItem cartItem = getShippingCartItem(shopContext, cart, shippingInfo);
					if (cartItem != null) {
						calculateItem(cartItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
						cart.set(cartItem);
					} else {
						cart.remove("shipping");
					}
				}
			} catch (Exception e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}
		}

		// waren und services inkl. (line)discount (als basis zur berechnung der
		// resource produkte) ---
		@SuppressWarnings("unused")
		Total goodsAndServices = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT,
				CartItemQualifier.VPRODUCT, CartItemQualifier.SERVICE, CartItemQualifier.SHIPPING));

		// TODO: cart discounts ---
		try {
			
			CartRuleContext cartRuleContext = cartRuleService.getNewCartRuleContext(shopContext, customer, cart);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		/*
		List<Coupon> coupons = cart.getCoupons();
		if (coupons != null) { 
			for (Coupon coupon : coupons) { 
				try { 
					Map<String, Object> context = new HashMap<String, Object>(); 
					context.put("cartService", this);
					context.put("shopContext", shopContext); 
					context.put("customer", customer); 
					context.put("cart", cart); 
					CouponResult couponResult = coupon.apply(context); 
					// CouponResult couponResult = coupon.apply(shopContext, 
					// customer, cart); 
					if (!couponResult.hasErrors()) { 
						if (couponResult.getDiscounts().containsKey("total")) { 
							BigDecimal discount = couponResult.getDiscounts().get("total");
							List<CartItem> cartDiscountItems = getCartDiscountItems(cart, goodsAndServices, discount); cart.addAll(cartDiscountItems); 
						} 
					} 
				} catch (Exception e) { 
					log.error(ExceptionUtils.getStackTrace(e)); 
				} 
			}
		}
		*/


		// summe alle discounts ---
		Total discount = calculateTotal(cart, Arrays.asList(CartItemQualifier.DISCOUNT));
		cart.setDiscount(discount);

		// summen ---
		cart.setValueOfGoods(goods.getGross());
		Total total = calculateTotal(cart,
				Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT, CartItemQualifier.SERVICE,
						CartItemQualifier.DISCOUNT, CartItemQualifier.RESOURCE, CartItemQualifier.SHIPPING));
		cart.setTotal(total);
	}

	private void calculateItem(CartItem item, String countryOfDelivery, TaxCode customerTaxCode) {
		BigDecimal totalPrice = item.getUnitPrice("std").multiply(item.getQuantity()).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		item.getTotalPrices().put("std", totalPrice);

		BigDecimal taxRate = taxService.getTaxRate(item.getTaxCode(), customerTaxCode, countryOfDelivery);
		item.setTaxPercent(taxRate.multiply(HUNDRED));
	}

	private Total calculateTotal(Cart cart, List<String> qualifiers) {
		TotalImpl total = new TotalImpl();

		Map<TaxCode, Line> lines = total.getLines();
		for (CartItem item : cart.getItems().values()) {
			if (qualifiers.contains(item.getQualifier())) {
				TaxCode taxCode = item.getTaxCode();
				BigDecimal taxPercent = item.getTaxPercent();
				BigDecimal gross = item.getTotalPrice("std").add(item.getDiscount());
				BigDecimal quantity = item.getQuantity();

				total.addGross(gross);
				total.addQuantity(quantity);

				Line line = lines.get(taxCode);
				if (line == null) {
					line = new LineImpl(taxCode, taxPercent);
					lines.put(taxCode, line);
				}
				line.addGross(gross);
				line.addQuantity(quantity);
			}
		}

		BigDecimal cent = new BigDecimal(100);
		for (Line line : lines.values()) {
			line.setNet(
					line.getGross().divide(line.getTaxPercent().add(cent).divide(cent), 2, BigDecimal.ROUND_HALF_UP));
			line.setTax(line.getGross().subtract(line.getNet()));
		}

		return total;
	}

	@Override
	public Messages check(ShopContext shopContext, Cart cart, Customer customer, boolean timing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrency(ShopContext shopContext, Cart cart, Customer customer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveCart(Customer customer, Cart cart) throws CartServiceException {
		if (StringUtils.isNotBlank(cart.getShopCode())) {
			mongoCartDaos.get(cart.getShopCode()).saveCart(cart);			
		} else {
			throw new CartServiceException("empty shop code!");
		}
	}

	// @Override
	// public void setStatus(Customer customer, Cart cart, String status) throws
	// CartServiceException {
	// cart.setStatus(status);
	// cart.setUpdatedBy(customer.getId());
	// }

	// @Override
	// @Deprecated
	// public Cart loadCart(String shopCode, String id) throws
	// CartServiceException {
	// return mongoCartDaos.get(shopCode).loadCart(id);
	// }

	@Override
	public Cart loadActiveCart(ShopContext context, Customer customer) throws CartServiceException {
		List<Cart> carts = mongoCartDaos.get(context.getShopCode()).loadCart(customer,
				Arrays.asList(context.getShopCode()), Arrays.asList("ACTIVE"));
		if (CollectionUtils.isNotEmpty(carts)) {
			return carts.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Cart loadCart(ShopContext context, String id) throws CartServiceException {
		return mongoCartDaos.get(context.getShopCode()).loadCart(id);
	}

	@Override
	public List<Cart> loadCart(ShopContext context, Customer customer, List<String> codes, List<String> status)
			throws CartServiceException {
		return mongoCartDaos.get(context.getShopCode()).loadCart(customer, codes, status);
	}

	@Override
	public void deleteCart(Cart cart) throws CartServiceException {
		mongoCartDaos.get(cart.getShopCode()).deleteCart(cart);
	}

	@Override
	public void merge(Cart storedCart, Cart cart) {
		// TODO Auto-generated method stub

	}

	@Override
	public Cart getNewCart(ShopContext shopContext) throws CartServiceException {
		return mongoCartDaos.get(shopContext.getShopCode()).getNewCart();
	}

	private CartItem getShippingCartItem(ShopContext shopContext, Cart cart, ShippingInfo shippingInfo) {

		// just testing ---

		if (BigDecimal.ZERO.compareTo(shippingInfo.getCharges()) == -1) {
			try {
				CartItem cartItem = cart.getNewItem("shipping", BigDecimal.ONE);
				cartItem.setId("shipping");
				cartItem.setQualifier(CartItemQualifier.SHIPPING);
				cartItem.setIsVisible(true);
				cartItem.setIsVolatile(false);
				cartItem.setIsImmutable(true);

				cartItem.setTaxCode(TaxCode.NORMAL);
				cartItem.setUnitPrice("std", shippingInfo.getCharges());

				cartItem.getTitle().put("title1", translationService.getMessage("product.cart.title1", "text",
						"shipping", null, "product.cart.title1", shopContext.getCurrentLocale()));
				cartItem.getTitle().put("title2", translationService.getMessage("product.cart.title2", "text",
						"shipping", null, "product.cart.title2", shopContext.getCurrentLocale()));
				cartItem.getTitle().put("title3", translationService.getMessage("product.cart.title3", "text",
						"shipping", null, "product.cart.title3", shopContext.getCurrentLocale()));

				return cartItem;
			} catch (Exception e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}

			// return new ShippingCartItem(shippingInfo.getCharges(),
			// shippingInfo.getTaxCode());
		}

		return null;
	}

	public void setMongoCartDaos(Map<String, MongoCartDao> mongoCartDaos) {
		this.mongoCartDaos = mongoCartDaos;
	}

	@Override
	public void setIndividualUse(Coupon coupon, String customerId, String orderId, Cart cart, String shopCode)
			throws CartServiceException {
		// TODO Auto-generated method stub
		throw new RuntimeException("not implemented");
	}

	@Override
	public void setStatus(Customer customer, Cart cart, String status) throws CartServiceException {
		// TODO Auto-generated method stub
		throw new RuntimeException("not implemented");		
	}
	
}