package com.bakerbeach.market.cart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bakerbeach.market.cart.api.model.CartRule.Intention;
import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleSet;
import com.bakerbeach.market.cart.api.model.CartRuleSetResult;
import com.bakerbeach.market.cart.api.model.CartRuleStore;
import com.bakerbeach.market.cart.api.model.RuleMessage;
import com.bakerbeach.market.cart.api.service.CartRuleAware;
import com.bakerbeach.market.cart.api.service.CartService;
import com.bakerbeach.market.cart.api.service.CartServiceException;
import com.bakerbeach.market.cart.api.service.RuleAware;
import com.bakerbeach.market.cart.dao.MongoCartDao;
import com.bakerbeach.market.cart.model.TotalImpl;
import com.bakerbeach.market.cart.model.TotalImpl.LineImpl;
import com.bakerbeach.market.cart.rules.SimpleCartRuleContextImpl;
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
	public CartRuleStore cartRuleStore;

	// @Autowired(required=false)
	// protected CartRuleService cartRuleService;

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

		Cart cart = getNewInstance(shopContext, customer);
		calculate(shopContext, cart, customer);
		return cart;
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

			messages.add(new MessageImpl(Message.TYPE_INFO, "successfully.added.item", cartItem.getId(),
					cartItem.getCode(), cartItem.getQuantity()));
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

				messages.addGlobalMessage(new MessageImpl(Message.TYPE_INFO, "successfully.updated.item", item.getId(),
						item.getCode(), item.getQuantity()));
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
		preCalculationHandler(shopContext, cart, customer);
		basicCalculation(shopContext, cart, customer);
		postCalculationHandler(shopContext, cart, customer);
	}

	private void preCalculationHandler(ShopContext shopContext, Cart cart, Customer customer) {
		// TODO Auto-generated method stub
	}

	private void postCalculationHandler(ShopContext shopContext, Cart cart, Customer customer) {
		// TODO Auto-generated method stub
	}

	private final void basicCalculation(ShopContext shopContext, Cart cart, Customer customer) {
		Map<String, CartItem> synchronizedMap = Collections.synchronizedMap(cart.getItems());

		// clear rule messages ---
		if (cart instanceof RuleAware) {
			((RuleAware) cart).getRuleMessages().clear();
		}

		// remove zero quantity and volatile items and clear line discounts ---
		Collection<String> toBeRemoved = new ArrayList<String>();
		synchronizedMap.forEach((k, i) -> {
			if (i.getQuantity().compareTo(BigDecimal.ZERO) < 1 || i.isVolatile()) {
				toBeRemoved.add(k);
			}
		});
		synchronizedMap.keySet().removeAll(toBeRemoved);

		// check min and max quantity ---
		synchronizedMap.forEach((k, i) -> {
			if (i.getMinQty() != null && i.getMaxQty() != null) {
				if (i.getQuantity().compareTo(i.getMaxQty()) > -1) {
					i.setQuantity(i.getMaxQty());
				} else if (i.getQuantity().compareTo(i.getMinQty()) == -1) {
					i.setQuantity(i.getMinQty());
				}
			}
		});

		// reset line discount ---
		synchronizedMap.forEach((k, i) -> {
			i.setDiscount(BigDecimal.ZERO);
		});

		// TODO: check for rules that may change the items/lines ---
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleSet cartRuleSet = ((CartRuleAware) cart).getCartRuleSet();
				cartRuleSet.init(cartRuleStore);

				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("cart", cart);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				CartRuleSetResult result = cartRuleSet.apply(Intention.LINE_CHANGES, context);
				
				CartItem item = createItem(cart, "foo", CartItemQualifier.PRODUCT, TaxCode.NORMAL, true, true, true,
						BigDecimal.ZERO, shopContext);
//				if (item != null) {
//					calculateItem(item, shopContext.getCountryOfDelivery(), customer.getTaxCode());
					cart.set(item);
//				}

			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		
		// zeilen berechnen (alle produkte) ---
		synchronizedMap.forEach((k, item) -> {
			calculateItem(item, shopContext.getCountryOfDelivery(), customer.getTaxCode());
		});

		Total goods = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT));

		// TODO: set payment ---
		cart.setPayment(BigDecimal.ZERO);

		// TODO: line-discount ---
		// ...

		// calculate (again) goods (now including line discounts) ---
		goods = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT));
		cart.setValueOfGoods(goods.getGross());

		// cart discount before shipping ---
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleSet cartRuleSet = ((CartRuleAware) cart).getCartRuleSet();
				cartRuleSet.init(cartRuleStore);

				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("cart", cart);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				CartRuleSetResult result = cartRuleSet.apply(Intention.DISCOUNT_ON_GOODS, context);
				BigDecimal discount = result.getTotal();
				List<RuleMessage> ruleMessages = result.getMessages();

				List<CartItem> discountItems = getCartDiscountItems(cart, "discount-1", goods, discount, shopContext);
				if (CollectionUtils.isNotEmpty(discountItems)) {
					for (CartItem discountItem : discountItems) {
						calculateItem(discountItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
						cart.set(discountItem);
					}
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		// shipping ---
		Total shippingGoods = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT));
		cart.setValueOfShippingGoods(shippingGoods.getGross());

		try {
			if (cart instanceof CartRuleAware) {
				CartRuleSet cartRuleSet = ((CartRuleAware) cart).getCartRuleSet();
				cartRuleSet.init(cartRuleStore);

				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("cart", cart);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				CartRuleSetResult result = cartRuleSet.apply(Intention.SHIPPING, context);
				BigDecimal shipping = result.getTotal();
				List<RuleMessage> ruleMessages = result.getMessages();
				
				CartItem shippingItem = createItem(cart, "shipping", CartItemQualifier.SHIPPING, TaxCode.NORMAL, true,
						true, true, shipping, shopContext);
				if (shippingItem != null) {
					calculateItem(shippingItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
					cart.set(shippingItem);
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		// TODO: shipping discount ---
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleSet cartRuleSet = ((CartRuleAware) cart).getCartRuleSet();
				cartRuleSet.init(cartRuleStore);

				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("cart", cart);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				CartRuleSetResult result = cartRuleSet.apply(Intention.DISCOUNT_ON_SHIPPING, context);
				BigDecimal shipping = result.getTotal();
				List<RuleMessage> ruleMessages = result.getMessages();

				CartItem shippingDiscountItem = createItem(cart, "discount-shipping", CartItemQualifier.SHIPPING, TaxCode.NORMAL, true,
						true, true, shipping, shopContext);
				if (shippingDiscountItem != null) {
					calculateItem(shippingDiscountItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
					cart.set(shippingDiscountItem);
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		// shipping gross price ---
		Total shipping = calculateTotal(cart, Arrays.asList(CartItemQualifier.SHIPPING));
		cart.setShipping(shipping.getGross());

		// waren und services inkl. (line)discount (als basis zur berechnung der
		// resource produkte) ---
		@SuppressWarnings("unused")
		Total goodsAndServices = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT,
				CartItemQualifier.VPRODUCT, CartItemQualifier.SERVICE, CartItemQualifier.SHIPPING));

		
		// global cart discount including shipping ans services ---
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleSet cartRuleSet = ((CartRuleAware) cart).getCartRuleSet();
				cartRuleSet.init(cartRuleStore);

				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("cart", cart);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				CartRuleSetResult result = cartRuleSet.apply(Intention.DISCOUNT_ON_GOODS_AND_SERVICES, context);
				BigDecimal discount = result.getTotal();
				List<RuleMessage> ruleMessages = result.getMessages();

				List<CartItem> discountItems = getCartDiscountItems(cart, "discount-2", goodsAndServices, discount, shopContext);
				if (CollectionUtils.isNotEmpty(discountItems)) {
					for (CartItem discountItem : discountItems) {
						calculateItem(discountItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
						cart.set(discountItem);
					}
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		// summe alle discounts ---
		Total discount = calculateTotal(cart, Arrays.asList(CartItemQualifier.DISCOUNT));
		cart.setDiscount(discount);

		// summen ---
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

	protected List<CartItem> getCartDiscountItems(Cart cart, String id, Total total, BigDecimal discount,
			ShopContext shopContext) {
		BigDecimal maxDiscount = total.getGross();
		BigDecimal discountRest = discount.min(maxDiscount);

		List<CartItem> items = new ArrayList<CartItem>();
		Collection<Line> lines = total.getLines().values();
		for (Iterator<Line> i = lines.iterator(); i.hasNext();) {
			Line line = (Line) i.next();

			BigDecimal resourceGross;
			if (i.hasNext()) {
				BigDecimal q = line.getGross().divide(maxDiscount, 5, BigDecimal.ROUND_HALF_UP);
				resourceGross = q.multiply(discountRest).setScale(2, BigDecimal.ROUND_HALF_UP);
				discountRest = discountRest.subtract(resourceGross);
			} else {
				resourceGross = discountRest;
			}

			CartItem item = cart.getNewItem(id, BigDecimal.ONE);
			item.setId(id + "-" + line.getTaxCode().name().toLowerCase());
			item.setQualifier(CartItemQualifier.DISCOUNT);
			item.setIsVisible(true);
			item.setIsVolatile(true);
			item.setIsImmutable(true);
			item.setTaxCode(line.getTaxCode());
			item.setUnitPrice("std", resourceGross);

			item.getTitle().put("title1", translationService.getMessage("product.cart.title1", "text", "discount", null,
					"product.cart.title1", shopContext.getCurrentLocale()));
			item.getTitle().put("title2", translationService.getMessage("product.cart.title2", "text", "discount", null,
					"product.cart.title2", shopContext.getCurrentLocale()));
			item.getTitle().put("title3", translationService.getMessage("product.cart.title3", "text", "discount", null,
					"product.cart.title3", shopContext.getCurrentLocale()));

			items.add(item);
		}

		return items;
	}

	protected CartItem createItem(Cart cart, String id, String qualifier, TaxCode taxCode, Boolean isVisible,
			Boolean isVolatile, Boolean isImmutable, BigDecimal stdUnitPrice, ShopContext shopContext) {
		try {
			CartItem item = cart.getNewItem(id, BigDecimal.ONE);
			item.setId(id);
			item.setQualifier(qualifier);
			item.setIsVisible(isVisible);
			item.setIsVolatile(isVolatile);
			item.setIsImmutable(isImmutable);
			item.setTaxCode(taxCode);
			item.setUnitPrice("std", stdUnitPrice);
			
			item.getTitle().put("title1", translationService.getMessage("product.cart.title1", "text", "shipping", null,
					"product.cart.title1", shopContext.getCurrentLocale()));
			item.getTitle().put("title2", translationService.getMessage("product.cart.title2", "text", "shipping", null,
					"product.cart.title2", shopContext.getCurrentLocale()));
			item.getTitle().put("title3", translationService.getMessage("product.cart.title3", "text", "shipping", null,
					"product.cart.title3", shopContext.getCurrentLocale()));
			
			return item;			
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	protected CartItem getShippingItem(Cart cart, BigDecimal value, ShopContext shopContext) {
		if (value != null && value.compareTo(BigDecimal.ZERO) == 1) {
			CartItem item = cart.getNewItem("shipping", BigDecimal.ONE);
			item.setId("shipping");
			item.setQualifier(CartItemQualifier.SHIPPING);
			item.setIsVisible(true);
			item.setIsVolatile(true);
			item.setIsImmutable(true);
			// TODO: tax code from
			item.setTaxCode(TaxCode.NORMAL);
			item.setUnitPrice("std", value);

			item.getTitle().put("title1", translationService.getMessage("product.cart.title1", "text", "shipping", null,
					"product.cart.title1", shopContext.getCurrentLocale()));
			item.getTitle().put("title2", translationService.getMessage("product.cart.title2", "text", "shipping", null,
					"product.cart.title2", shopContext.getCurrentLocale()));
			item.getTitle().put("title3", translationService.getMessage("product.cart.title3", "text", "shipping", null,
					"product.cart.title3", shopContext.getCurrentLocale()));

			return item;
		} else {
			return null;
		}
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
		cart.setStatus(status);
		saveCart(customer, cart);
	}

}