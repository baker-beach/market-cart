package com.bakerbeach.market.cart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bakerbeach.market.cart.api.model.CartRule;
import com.bakerbeach.market.cart.api.model.CartRule.Intention;
import com.bakerbeach.market.cart.api.model.CartRuleContext;
import com.bakerbeach.market.cart.api.model.CartRuleResult;
import com.bakerbeach.market.cart.api.model.CartRuleSet;
import com.bakerbeach.market.cart.api.model.CartRuleStore;
import com.bakerbeach.market.cart.api.service.CartRuleAware;
import com.bakerbeach.market.cart.api.service.CartService;
import com.bakerbeach.market.cart.api.service.CartServiceException;
import com.bakerbeach.market.cart.dao.MongoCartDao;
import com.bakerbeach.market.cart.model.TotalImpl;
import com.bakerbeach.market.cart.model.TotalImpl.LineImpl;
import com.bakerbeach.market.cart.rules.CartRuleDao;
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
import com.bakerbeach.market.tax.api.service.TaxService;
import com.bakerbeach.market.translation.api.service.TranslationService;
import com.bakerbeach.market.xcatalog.service.XCatalogService;

public class XCartServiceImpl implements CartService {
	protected static final Logger log = LoggerFactory.getLogger(XCartServiceImpl.class);
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	protected Map<String, MongoCartDao> mongoCartDaos;

	@Autowired
	private Datastore shopDatastore;

	@Autowired
	private TaxService taxService;

	@Autowired
	protected TranslationService translationService;

	@Autowired
	protected XCatalogService catalogService;

	@Autowired
	public CartRuleStore cartRuleStore;

	@Autowired
	private CartRuleDao cartRuleDao;

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

			messages.add(
					new MessageImpl("add", Message.TYPE_INFO, "successfully.added.item", Arrays.asList(Message.TAG_BOX),
							Arrays.asList(cartItem.getId(), cartItem.getCode(), cartItem.getQuantity())));
			return messages;
		} catch (CartServiceException e) {
			return e.getMessages();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));

			Messages messages = new MessagesImpl();
			messages.add(
					new MessageImpl("add", Message.TYPE_ERROR, "cart.error.", Arrays.asList(Message.TAG_BOX), null));
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

			if (item != null && !item.isImmutable()) {
				if (quantity.compareTo(BigDecimal.ZERO) < 1) {
					cart.remove(item);
				} else if (quantity.compareTo(item.getMinQty()) > -1 && quantity.compareTo(item.getMaxQty()) < 1) {
					item.setQuantity(quantity);
				}

				messages.addGlobalMessage(new MessageImpl("set", Message.TYPE_INFO, "successfully.updated.item",
						Arrays.asList(Message.TAG_BOX),
						Arrays.asList(item.getId(), item.getCode(), item.getQuantity())));
			}

			return messages;
		} catch (CartServiceException e) {
			return e.getMessages();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));

			Messages messages = new MessagesImpl();
			messages.addGlobalMessage(
					new MessageImpl("set", Message.TYPE_ERROR, "cart.error", Arrays.asList(Message.TAG_BOX), null));
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
		calculate(shopContext, cart, customer, new MessagesImpl());
	}

	@Override
	public final synchronized void calculate(ShopContext shopContext, Cart cart, Customer customer, Messages messages) {
		Map<String, CartItem> synchronizedMap = Collections.synchronizedMap(cart.getItems());

		Messages cartMessages = new MessagesImpl();

		Map<String, CartRuleResult> ruleResults = new LinkedHashMap<>();

		Date now = new Date();

		// clear rule messages ---
		if (cart instanceof CartRuleAware) {
			((CartRuleAware) cart).getMessages().clear();
		}

		// check cart rules ---
		if (cart instanceof CartRuleAware) {
			checkCartRules(cart, customer, now);
			// messages.add(checkCartRules(cart, customer, now));
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

		// rules that may change the items/lines ---
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("shopCode", shopContext.getShopCode());
				context.put("catalogService", catalogService);
				context.put("shopDatastore", shopDatastore);
				context.put("shippingAddress", shopContext.getShippingAddress());

				Map<String, CartRuleResult> currentRuleResults = applyCartRules(cart, customer, Intention.LINE_CHANGES,
						context, ruleResults);

				for (CartRuleResult ruleResult : currentRuleResults.values()) {
					if (ruleResult.containsKey("newCartItem")) {
						CartItem item = (CartItem) ruleResult.get("newCartItem");

						item.getTitle().put("title1", translationService.getMessage("product.cart.title.1", "text",
								item.getCode(), null, "product.cart.title1", shopContext.getCurrentLocale()));
						item.getTitle().put("title2", translationService.getMessage("product.cart.title.2", "text",
								item.getCode(), null, "product.cart.title2", shopContext.getCurrentLocale()));
						item.getTitle().put("title3", translationService.getMessage("product.cart.title.3", "text",
								item.getCode(), null, "product.cart.title3", shopContext.getCurrentLocale()));

						cart.set(item);
					}
				}

				ruleResults.putAll(currentRuleResults);
				messages.add(getRuleResultsMessages(currentRuleResults));
				cartMessages.add(getRuleResultsMessages(currentRuleResults, Arrays.asList("cart")));
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

		// discount-1 (discount on goods, before shipping) ---
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("shopCode", shopContext.getShopCode());
				context.put("catalogService", catalogService);
				context.put("shopDatastore", shopDatastore);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				Map<String, CartRuleResult> currentRuleResults = applyCartRules(cart, customer,
						Intention.DISCOUNT_ON_GOODS, context, ruleResults);
				BigDecimal discount = getRuleResultsTotal(currentRuleResults);

				List<CartItem> discountItems = getCartDiscountItems(cart, "discount-1", goods, discount, shopContext);
				if (CollectionUtils.isNotEmpty(discountItems)) {
					for (CartItem discountItem : discountItems) {
						if (discountItem.getTotalPrice("std") != BigDecimal.ZERO) {
							calculateItem(discountItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
							cart.set(discountItem);
						}
					}
				}

				ruleResults.putAll(currentRuleResults);
				messages.add(getRuleResultsMessages(currentRuleResults));
				cartMessages.add(getRuleResultsMessages(currentRuleResults, Arrays.asList("cart")));
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		// shipping ---
		Total shippingGoods = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT));
		cart.setValueOfShippingGoods(shippingGoods.getGross());

		try {
			if (cart instanceof CartRuleAware) {
				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("shopCode", shopContext.getShopCode());
				context.put("catalogService", catalogService);
				context.put("shopDatastore", shopDatastore);
				context.put("cart", cart);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				Map<String, CartRuleResult> currentRuleResults = applyCartRules(cart, customer, Intention.SHIPPING,
						context, ruleResults);
				BigDecimal shipping = getRuleResultsTotal(currentRuleResults);

				CartItem shippingItem = createItem(cart, "shipping", CartItemQualifier.SHIPPING, TaxCode.NORMAL,
						BigDecimal.ONE, true, true, true, shipping, shopContext);
				if (shippingItem != null) {
					calculateItem(shippingItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
					cart.set(shippingItem);
				}

				ruleResults.putAll(currentRuleResults);
				messages.add(getRuleResultsMessages(currentRuleResults));
				cartMessages.add(getRuleResultsMessages(currentRuleResults, Arrays.asList("cart")));
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		// TODO: shipping discount ---
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("shopCode", shopContext.getShopCode());
				context.put("catalogService", catalogService);
				context.put("shopDatastore", shopDatastore);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				Map<String, CartRuleResult> currentRuleResults = applyCartRules(cart, customer,
						Intention.DISCOUNT_ON_SHIPPING, context, ruleResults);
				BigDecimal shipping = getRuleResultsTotal(currentRuleResults);

				CartItem shippingDiscountItem = createItem(cart, "discount-shipping", CartItemQualifier.SHIPPING,
						TaxCode.NORMAL, BigDecimal.ONE, true, true, true, shipping, shopContext);
				if (shippingDiscountItem != null && shippingDiscountItem.getTotalPrice("std") != BigDecimal.ZERO) {
					calculateItem(shippingDiscountItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
					cart.set(shippingDiscountItem);
				}
				
				ruleResults.putAll(currentRuleResults);
				messages.add(getRuleResultsMessages(currentRuleResults));
				cartMessages.add(getRuleResultsMessages(currentRuleResults, Arrays.asList("cart")));
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		// shipping gross price ---
		Total shipping = calculateTotal(cart, Arrays.asList(CartItemQualifier.SHIPPING));
		cart.setShipping(shipping.getGross());

		// waren und services inkl. (line)discount (als basis zur berechnung der
		// resource produkte) ---
		Total goodsAndServices = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT,
				CartItemQualifier.VPRODUCT, CartItemQualifier.SERVICE, CartItemQualifier.SHIPPING));

		// global cart discount including shipping ans services ---
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleContext context = new SimpleCartRuleContextImpl();
				context.put("shopCode", shopContext.getShopCode());
				context.put("catalogService", catalogService);
				context.put("shopDatastore", shopDatastore);
				context.put("customer", customer);
				context.put("shippingAddress", shopContext.getShippingAddress());

				Map<String, CartRuleResult> currentRuleResults = applyCartRules(cart, customer,
						Intention.DISCOUNT_ON_GOODS_AND_SERVICES, context, ruleResults);
				BigDecimal discount = getRuleResultsTotal(currentRuleResults);

				List<CartItem> discountItems = getCartDiscountItems(cart, "discount-2", goodsAndServices, discount,
						shopContext);
				if (CollectionUtils.isNotEmpty(discountItems)) {
					for (CartItem discountItem : discountItems) {
						if (discountItem.getTotalPrice("std") != BigDecimal.ZERO) {
							calculateItem(discountItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
							cart.set(discountItem);
						}
					}
				}

				ruleResults.putAll(currentRuleResults);
				messages.add(getRuleResultsMessages(currentRuleResults));
				cartMessages.add(getRuleResultsMessages(currentRuleResults, Arrays.asList("cart")));
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

		// removed failed coupon rules ---
		if (cart instanceof CartRuleAware) {
			CartRuleSet ruleSet = getCartRuleSet(cart);
			if (ruleSet != null) {
				Set<String> tbr = new HashSet<>();
				for (Entry<String, CartRule> entry : ruleSet.getCodeRules().entrySet()) {
					if (entry.getValue().getStatus().equals(CartRule.Status.DISABLED)) {
						tbr.add(entry.getKey());
					}
				}

				for (String key : tbr) {
					ruleSet.getCodeRules().remove(key);
				}
			}
		}

		// clear rule messages ---
		if (cart instanceof CartRuleAware) {
			((CartRuleAware) cart).getMessages().add(cartMessages);
		}

	}

	private Messages getRuleResultsMessages(Map<String, CartRuleResult> ruleResults, List<String> tags) {
		Messages messages = new MessagesImpl();
		if (MapUtils.isNotEmpty(ruleResults)) {
			for (CartRuleResult result : ruleResults.values()) {
				for (Message message : result.getMessages().getAll()) {
					if (message != null) {
						if (CollectionUtils.isNotEmpty(tags)) {
							if (message.getTags() != null && CollectionUtils.containsAny(message.getTags(), tags)) {
								messages.add(message);
							}
						} else {
							messages.add(message);
						}
					}
				}
			}
		}

		return messages;
	}

	private Messages getRuleResultsMessages(Map<String, CartRuleResult> ruleResults) {
		return getRuleResultsMessages(ruleResults, null);
	}

	private BigDecimal getRuleResultsTotal(Map<String, CartRuleResult> ruleResults) {
		BigDecimal total = BigDecimal.ZERO;
		if (MapUtils.isNotEmpty(ruleResults)) {
			for (CartRuleResult result : ruleResults.values()) {
				if (result.getValues().containsKey("total")) {
					total = total.add(result.getValues().get("total"));
				}
			}
		}

		return total;
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
		BigDecimal discountRest = discount.min(maxDiscount).setScale(2, BigDecimal.ROUND_HALF_UP);

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

	protected CartItem createItem(Cart cart, String id, String qualifier, TaxCode taxCode, BigDecimal quantity,
			Boolean isVisible, Boolean isVolatile, Boolean isImmutable, BigDecimal stdUnitPrice,
			ShopContext shopContext) {
		try {
			CartItem item = cart.getNewItem(id, quantity);
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

	@Override
	public void addCodeRule(Cart cart, String key, CartRule rule) {
		CartRuleSet ruleSet = getCartRuleSet(cart);
		if (ruleSet != null) {
			ruleSet.addCodeRule(key, rule);
		}
	}

	@Override
	public void clearCodeRules(Cart cart) {
		CartRuleSet ruleSet = getCartRuleSet(cart);
		if (ruleSet != null) {
			ruleSet.clearCodeRules();
		}
	}

	@Override
	public Map<String, CartRule> getCodeRules(Cart cart) {
		CartRuleSet ruleSet = getCartRuleSet(cart);
		if (ruleSet != null) {
			return ruleSet.getCodeRules();
		}

		return null;
	}

	@Override
	public void setRuleUse(ShopContext context, Cart cart, Customer customer, String qualifier)
			throws CartServiceException {
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleSet ruleSet = ((CartRuleAware) cart).getCartRuleSet();
				for (Entry<String, CartRule> entry : ruleSet.entrySet()) {
					String key = entry.getKey();
					CartRule rule = entry.getValue();

					if (CartRule.Status.PASSED.equals(rule.getStatus())) {
						if (rule.getMaxIndividualUse() != null) {
							cartRuleDao.setUse(key, customer.getId(), 1, rule.getMaxIndividualUse(), qualifier,
									new Date());
							rule.setIsUsed(true);
						}
					}
				}
			}
		} catch (Exception e) {
			log.info(ExceptionUtils.getMessage(e));
			throw new CartServiceException();
		}
	}

	@Override
	public void unsetRuleUse(ShopContext context, Cart cart, Customer customer, String qualifier) {
		try {
			if (cart instanceof CartRuleAware) {
				CartRuleSet ruleSet = ((CartRuleAware) cart).getCartRuleSet();
				for (Entry<String, CartRule> entry : ruleSet.entrySet()) {
					String key = entry.getKey();
					CartRule rule = entry.getValue();

					if (rule.getMaxIndividualUse() != null && rule.getIsUsed()) {
						cartRuleDao.unsetUse(key, customer.getId(), 1, qualifier, new Date());
						rule.setIsUsed(false);
					}
				}
			}
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
	}

	private Map<String, CartRuleResult> applyCartRules(Cart cart, Customer customer, Intention intention,
			CartRuleContext context, Map<String, CartRuleResult> results) {
		Map<String, CartRuleResult> currentResults = new LinkedHashMap<>();

		CartRuleSet ruleSet = getCartRuleSet(cart);
		if (ruleSet != null) {
			for (Entry<String, CartRule> entry : ruleSet.entrySet()) {
				CartRule rule = entry.getValue();

				if (rule.getIntentions().contains(intention)) {
					if (!CartRule.Status.DISABLED.equals(rule.getStatus())) {
						CartRuleResult result = rule.apply(cart, intention, context, results);
						currentResults.put(result.getId(), result);
					}
				}
			}
		}

		return currentResults;
	}

	@Override
	public CartRule getCodeRuleInstance(String couponCode) {
		return cartRuleStore.getCodeRuleInstance(couponCode);
	}

	@Override
	public Messages checkCartRules(Cart cart, Customer customer, Date date) {
		Messages messages = new MessagesImpl();

		CartRuleSet ruleSet = getCartRuleSet(cart);
		if (ruleSet != null) {
			for (Entry<String, CartRule> entry : ruleSet.entrySet()) {
				String key = entry.getKey();
				CartRule rule = entry.getValue();
				messages.add(checkCartRule(key, rule, date, customer));
			}
		}

		return messages;
	}

	@Override
	public Message checkCartRule(String key, CartRule rule, Date date, Customer customer) {
		if (!date.before(rule.getEnd())) {
			rule.setStatus(CartRule.Status.DISABLED);
			return rule.getDisabledMessage();
		}

		if (date.before(rule.getStart())) {
			rule.setStatus(CartRule.Status.FAILED);
			return rule.getFailedMessage();
		}

		if (rule.getMaxIndividualUse() != null) {
			if (customer != null) {
				if (!customer.getId().equals(rule.getCustomerId()) || rule.getUseCount() == null) {
					rule.setUseCount(cartRuleDao.getUseCount(key, customer.getId()));
					rule.setCustomerId(customer.getId());
				}
				if (rule.getUseCount() >= rule.getMaxIndividualUse()) {
					rule.setStatus(CartRule.Status.DISABLED);
					return rule.getDisabledMessage();
				}
			}
		}

		return null;
	}

	public Message __checkCartRule(String key, CartRule rule, Date date, Customer customer) {
		if (!date.before(rule.getStart()) && date.before(rule.getEnd())) {
			if (rule.getMaxIndividualUse() != null) {
				if (customer != null) {
					if (!customer.getId().equals(rule.getCustomerId()) || rule.getUseCount() == null) {
						rule.setUseCount(cartRuleDao.getUseCount(key, customer.getId()));
						rule.setCustomerId(customer.getId());
					}
				}

				if (rule.getMaxIndividualUse() > rule.getUseCount()) {
					rule.setStatus(CartRule.Status.ENABLED);
					// TODO: get code,tags and args from rule implementation
					return new MessageImpl(key, Message.TYPE_INFO, rule.getId(), Arrays.asList(Message.TAG_BOX),
							Arrays.asList(CartRule.Status.ENABLED));
				} else {
					rule.setStatus(CartRule.Status.DISABLED);
					// TODO: get code,tags and args from rule implementation
					return new MessageImpl(key, Message.TYPE_INFO, rule.getId(), Arrays.asList(Message.TAG_BOX),
							Arrays.asList(CartRule.Status.DISABLED));
				}
			} else {
				rule.setStatus(CartRule.Status.ENABLED);
				// TODO: get code,tags and args from rule implementation
				return new MessageImpl(key, Message.TYPE_INFO, rule.getId(), Arrays.asList(Message.TAG_BOX),
						Arrays.asList(CartRule.Status.ENABLED));
			}
		} else {
			rule.setStatus(CartRule.Status.DISABLED);
			// TODO: get code,tags and args from rule implementation
			return new MessageImpl(key, Message.TYPE_INFO, rule.getId(), Arrays.asList(Message.TAG_BOX),
					Arrays.asList(CartRule.Status.DISABLED));
		}
	}

	public void setMongoCartDaos(Map<String, MongoCartDao> mongoCartDaos) {
		this.mongoCartDaos = mongoCartDaos;
	}

	protected CartRuleSet getCartRuleSet(Cart cart) {
		if (cart instanceof CartRuleAware) {
			CartRuleSet cartRuleSet = ((CartRuleAware) cart).getCartRuleSet();

			// TODO: add some caching
			for (String id : cartRuleStore.getCommonRuleIds()) {
				if (!cartRuleSet.containsKey(id)) {
					CartRule instance = cartRuleStore.getInstance(id);
					cartRuleSet.addCommonRule(id, instance);
				}
			}

			return cartRuleSet;
		} else {
			return null;
		}
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