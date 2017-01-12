package com.bakerbeach.market.cart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bakerbeach.market.cart.api.service.CartService;
import com.bakerbeach.market.cart.api.service.CartServiceException;
import com.bakerbeach.market.cart.coupon.CouponStore;
import com.bakerbeach.market.cart.coupon.SimpleCouponDao;
import com.bakerbeach.market.cart.model.CartItemImpl;
import com.bakerbeach.market.cart.model.CartItemImpl.CartItemComponentImpl;
import com.bakerbeach.market.cart.model.CartItemImpl.CartItemOptionImpl;
import com.bakerbeach.market.cart.model.DiscountCartItemImpl;
import com.bakerbeach.market.cart.model.ShippingCartItem;
import com.bakerbeach.market.cart.model.SimpleCartImpl;
import com.bakerbeach.market.cart.service.TotalImpl.LineImpl;
import com.bakerbeach.market.commons.MessageImpl;
import com.bakerbeach.market.commons.MessagesImpl;
import com.bakerbeach.market.commons.ServiceException;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.CartItem.CartItemComponent;
import com.bakerbeach.market.core.api.model.CartItem.CartItemOption;
import com.bakerbeach.market.core.api.model.CartItemQualifier;
import com.bakerbeach.market.core.api.model.Coupon;
import com.bakerbeach.market.core.api.model.CouponError;
import com.bakerbeach.market.core.api.model.CouponResult;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.core.api.model.Message;
import com.bakerbeach.market.core.api.model.Messages;
import com.bakerbeach.market.core.api.model.ShopContext;
import com.bakerbeach.market.core.api.model.TaxCode;
import com.bakerbeach.market.core.api.model.Total;
import com.bakerbeach.market.core.api.model.Total.Line;
import com.bakerbeach.market.shipping.api.model.ShippingContext;
import com.bakerbeach.market.shipping.api.model.ShippingInfo;
import com.bakerbeach.market.shipping.api.service.ShippingService;
import com.bakerbeach.market.tax.api.service.TaxService;

public class CartServiceImpl implements CartService {
	protected static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	@Autowired
	private TaxService taxService;

	@Autowired
	private ShippingService shippingService;

	@Autowired
	protected Datastore datastore;

	protected CouponStore couponStore;

	protected SimpleCouponDao couponDao;
	
	@Override
	public Cart getInstance(Customer customer) {
		try {
			if (!(customer.isAnonymousCustomer())) {
				Cart cart = loadActiveCart(customer);
				if (cart != null) {
					return cart;					
				}
			}
		} catch (CartServiceException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}

		return getNewInstance(customer);
	}
	
	@Override
	public Cart getNewInstance(Customer customer) {
		Cart cart = getNewCart();
		cart.setCustomerId(customer.getId());
		cart.setCreatedBy(customer.getId());
		cart.setStatus("ACTIVE");
		return cart;
	}

	protected void addCartItemPrecheck(ShopContext shopContext, Cart cart, CartItem newCartItem)
			throws CartServiceException {
	}

	@Override
	public final synchronized Messages addCartItem(ShopContext shopContext, Cart cart, CartItem cartItem)
			throws CartServiceException {

		try {
			Messages messages = new MessagesImpl();

			// TODO: optionally check inventory
			// TODO: optionally set reservation
			// TODO: check or create unique id for the entry

			addCartItemPrecheck(shopContext, cart, cartItem);

			// TODO: better id generation - hash item hash with all the options
			cartItem.setId(UUID.randomUUID().toString());
			cart.add(cartItem);

			messages.add(new MessageImpl(Message.TYPE_INFO, "Cart item successfully added.", cartItem.getGtin(),
					cartItem.getBrand(), cartItem.getQuantity()));
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

	@Override
	public final synchronized void removeCartItem(Cart cart, CartItem cartItem) {
		cart.remove(cartItem);
	}

	@Override
	public final synchronized void clear(Cart cart) {
		cart.clear();
	}

	@Override
	public final synchronized Messages setQuantity(Cart cart, String id, BigDecimal quantity)
			throws CartServiceException {
		try {
			Messages messages = new MessagesImpl();

			// TODO: optionally check inventory
			// TODO: optionally set reservation
			if (false)
				throw new ServiceException();

			CartItem item = cart.findItemById(id);
			if (item != null) {
				if (quantity.compareTo(BigDecimal.ZERO) == 0) {
					cart.remove(item);
				} else {
					item.setQuantity(quantity);
				}
			}

			messages.addGlobalMessage(
					new MessageImpl(Message.TYPE_INFO, "Cart item successfully updated.", item.getId()));
			return messages;
		} catch (ServiceException e) {
			return e.getMessages();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));

			Messages messages = new MessagesImpl();
			messages.addGlobalMessage(new MessageImpl(Message.TYPE_ERROR, "cart.error"));
			throw new CartServiceException(messages);
		}
	}

	@Override
	public final synchronized void calculate(ShopContext shopContext, Cart cart, Customer customer) {

		// remove zero quantity and volatile items and clear line discounts ---
		List<CartItem> toBeRemoved = new ArrayList<CartItem>();
		for (CartItem cartItem : cart.getCartItems()) {
			if (cartItem.getQuantity().compareTo(BigDecimal.ZERO) < 1) {
				toBeRemoved.add(cartItem);
			} else if (cartItem.isVolatile()) {
				toBeRemoved.add(cartItem);
			} else {
				cartItem.setDiscount(BigDecimal.ZERO);
			}
		}
		cart.getCartItems().removeAll(toBeRemoved);

		// line discounts auf Null ---
		for (CartItem item : cart.getCartItems()) {
			item.setDiscount(BigDecimal.ZERO);
		}

		// zeilen berechnen (alle produkte) ---
		for (CartItem item : cart.getCartItems()) {
			calculateItem(item, shopContext.getCountryOfDelivery(), customer.getTaxCode());
		}

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
		if (!cart.getCartItems().isEmpty()) {
			try {
				ShippingContext shippingContext = (ShippingContext) shopContext.getSessionData()
						.get(ShippingContext.CONTEXT_KEY);
				if (shippingContext == null) {
					shippingContext = shippingService.createShippingContext(shopContext, customer, cart);
					shopContext.getSessionData().put(ShippingContext.CONTEXT_KEY, shippingContext);
				}

				if (shippingService.checkShippingContext(shopContext, shippingContext)) {
					ShippingInfo shippingInfo = shippingService.apply(shippingContext);

					CartItem cartItem = getShipmentCartItem(shippingInfo);
					if (cartItem != null) {
						calculateItem(cartItem, shopContext.getCountryOfDelivery(), customer.getTaxCode());
						cart.add(cartItem);
					}
				}

			} catch (Exception e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}
		}

		// waren und services inkl. (line)discount (als basis zur berechnung der
		// resource produkte) ---
		Total goodsAndServices = calculateTotal(cart, Arrays.asList(CartItemQualifier.PRODUCT,
				CartItemQualifier.VPRODUCT, CartItemQualifier.SERVICE, CartItemQualifier.SHIPPING));

		// TODO: cart discounts ---
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
							
							List<CartItem> cartDiscountItems = getCartDiscountItems(cart, goodsAndServices, discount);
							cart.addAll(cartDiscountItems);
						}
					}
				} catch (Exception e) {
					log.error(ExceptionUtils.getStackTrace(e));
				}
			}
		}
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
		BigDecimal totalPrice = item.getUnitPrice().multiply(item.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP);
		item.setTotalPrice(totalPrice);
		BigDecimal taxRate = taxService.getTaxRate(item.getTaxCode(), customerTaxCode, countryOfDelivery);
		item.setTaxPercent(taxRate.multiply(HUNDRED));
	}

	private CartItem getShipmentCartItem(ShippingInfo shippingInfo) {
		if (BigDecimal.ZERO.compareTo(shippingInfo.getCharges()) == -1) {
			return new ShippingCartItem(shippingInfo.getCharges(), shippingInfo.getTaxCode());
		}

		return null;
	}

	private List<CartItem> getCartDiscountItems(Cart cart, Total goodsAndServices, BigDecimal discount) {
		BigDecimal maxDiscount = goodsAndServices.getGross();
		BigDecimal discountRest = discount.min(maxDiscount);

		List<CartItem> items = new ArrayList<CartItem>();
		Collection<Line> lines = goodsAndServices.getLines().values();
		for (Iterator<Line> i = lines.iterator(); i.hasNext();) {
			Line line = (Line) i.next();

			BigDecimal resourceGross;
			if (i.hasNext()) {
				BigDecimal q = line.getGross().divide(maxDiscount, 4, BigDecimal.ROUND_HALF_UP);
				resourceGross = q.multiply(discountRest).setScale(2, BigDecimal.ROUND_HALF_UP);
				discountRest = discountRest.add(resourceGross);
			} else {
				resourceGross = discountRest;
			}

			DiscountCartItemImpl item = new DiscountCartItemImpl();
			item.setTaxCode(line.getTaxCode());
			item.setTaxPercent(line.getTaxPercent());
			item.setUnitPrice(resourceGross);
			item.setTotalPrice(resourceGross);

			items.add(item);
		}

		return items;
	}

	private Total calculateTotal(Cart cart, List<String> qualifiers) {
		TotalImpl total = new TotalImpl();

		Map<TaxCode, Line> lines = total.getLines();
		for (CartItem item : cart.getCartItems()) {
			if (qualifiers.contains(item.getQualifier())) {
				TaxCode taxCode = item.getTaxCode();
				BigDecimal taxPercent = item.getTaxPercent();
				BigDecimal gross = item.getTotalPrice().add(item.getDiscount());
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
	public void merge(Cart source, Cart target) {
		if (source != null && target != null) {			
			// TODO: a better merge (check if item is already available)
			List<String> qualifiers = Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT);
			for (CartItem item : source.getCartItems()) {
				if (qualifiers.contains(item.getQualifier())) {
					target.add(item);
				}
			}

		}
	}
	
//	public void refresh(Cart cart, List<Product> products) {
//
//		Map<String, Product> map = new HashMap<String, Product>();
//		for (Product p : products) {
//			map.put(p.getGtin(), p);
//		}
//		
//		for (CartItem i : cart.getCartItems()) {
//			if (map.containsKey(i.getGtin())) {
//				Product p = map.get(i.getGtin());
//				
//				CartItem cartItem = getNewCartItem(i.getGtin(), i.getQuantity());
//				
//				if (p instanceof BundleProduct) {
//					BundleProduct b = (BundleProduct) p;
//					
//					
//					for (BundleComponent component : b.getComponents()) {
//						String componentName = component.getName();
//						
//						CartItemComponent c = i.getComponents().get(componentName);
//						for (BundleOption o : component.getOptions()) {
//							if (c.getOptions().containsKey(o.getGtin())) {
//								
//							}
//						}
//						
//						
//					}
//					
//				}
//				
//				
//				
//			}
//		}
//		
//		for (Product p : products) {
//			
//		}
//		
//		
//	}
	
	@Override
	public Cart getNewCart() {
		return new SimpleCartImpl();
	}
	
	public CartItem getNewCartItem(String gtin, BigDecimal quantity) {
		CartItemImpl item = new CartItemImpl(gtin, quantity);
		return item;
	};

	public CartItemComponent getNewCartItemComponent(String name) {
		return new CartItemComponentImpl(name);
	}

	public CartItemOption getNewCartItemOption(String gtin) {
		return new CartItemOptionImpl(gtin);
	}

	@Override
	public Cart loadCart(String id) throws CartServiceException {
		try {
			final Query<SimpleCartImpl> query = datastore.createQuery(SimpleCartImpl.class).field("_id")
					.equal(new ObjectId(id));
			Cart cart = query.get();
			
			return cart;
		} catch (Exception e) {
			throw new CartServiceException();
		}
	}

	@Override
	public Cart loadActiveCart(Customer customer) throws CartServiceException {
		List<Cart> carts = loadCart(customer, Arrays.asList("ACTIVE"));
		if (CollectionUtils.isNotEmpty(carts)) {
			return carts.get(0);
		}
		
		return null;
	}
	
	@Override
	public List<Cart> loadCart(Customer customer) throws CartServiceException {
		return loadCart(customer, Arrays.asList("ACTIVE"));
	}
	
	@Override
	public List<Cart> loadCart(Customer customer, List<String> status) throws CartServiceException {
		try {
			final Query<SimpleCartImpl> query = datastore.createQuery(SimpleCartImpl.class).field("customer_id")
					.equal(customer.getId());
			if (status != null && !status.isEmpty()) {
				query.field("status").in(status);
			}
			query.order("-updated_at");
			
			List<SimpleCartImpl> carts = query.asList();
			
			return new ArrayList<Cart>(carts);
		} catch (Exception e) {
			throw new CartServiceException();
		}
		
	}

	@Override
	public void saveCart(Customer customer, Cart cart) throws CartServiceException {
		try {
			cart.setUpdatedBy(customer.getId());
			datastore.save(cart);
		} catch (ConcurrentModificationException e) {
			throw new CartServiceException();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			throw new CartServiceException();
		}
	}
	
	@Deprecated
	@Override
	public void saveCart(Cart cart) throws CartServiceException {
		try {
			datastore.save(cart);
		} catch (ConcurrentModificationException e) {
			throw new CartServiceException();
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			throw new CartServiceException();
		}
	}

	@Override
	public void deleteCart(Cart cart) throws CartServiceException {
		try {
			datastore.delete(cart);
		} catch (Exception e) {
			throw new CartServiceException();
		}
	}
	
	@Override
	public Messages check(ShopContext shopContext, Cart cart, Customer customer, boolean timing) {
		// TODO: implement
		return null;
	}

	@Override
	public void setCurrency(ShopContext shopContext, Cart cart, Customer customer) {
		for (CartItem item : cart.getCartItems()) {

			BigDecimal price = item.getUnitPrices().get(shopContext.getCurrentCurrency().getIsoCode());
			if (price != null) {
				item.setUnitPrice(price);
				item.setTotalPrice(price.multiply(item.getQuantity()));
			}

			for (CartItemComponent cic : item.getComponents().values()) {
				for (CartItemOption cio : cic.getOptions().values()) {
					BigDecimal cioPrice = cio.getUnitPrices().get(shopContext.getCurrentCurrency().getIsoCode());
					if (cioPrice != null) {
						cio.setUnitPrice(cioPrice);
						cio.setTotalPrice(cioPrice.multiply(new BigDecimal(cio.getQuantity())));
					}
				}
			}

		}
	}

	@Override
	public Integer getIndividualUseCount(String code, String customerId) {
		Integer count = couponDao.getUseCount(code, customerId);
		return count;
	}

	@Override
	public void setIndividualUse(Coupon coupon, String customerId, String orderId, Cart cart, String shopCode)
			throws CartServiceException {
		try {
			couponDao.setUse(coupon.getCode(), customerId, 1, coupon.getMaxIndividualUse(), orderId, new Date());
		} catch (Exception e) {
			Messages messages = new MessagesImpl();
			messages.add(new MessageImpl(Message.TYPE_ERROR, "error.order.coupon.maxUses", coupon.getCode()));

			throw new CartServiceException(messages);
		}
	};

	@Override
	public Coupon getCoupon(String couponCode) {
		Coupon coupon = couponStore.getCoupon(couponCode);
		if (coupon != null) {
			if (coupon.checkTime(new Date())) {
				return coupon;
			}
		}

		return null;
	}

	@Override
	public Coupon getCoupon(String couponCode, Customer customer) {
		Coupon coupon = couponStore.getCoupon(couponCode);
		if (coupon != null) {
			Boolean test = true;
			
			if (!coupon.checkTime(new Date())) {
				test = false;
			}
			
			if (coupon.getMaxIndividualUse() != null) {
				Integer count = getIndividualUseCount(coupon.getCode(), customer.getId());
				if (count >= coupon.getMaxIndividualUse()) {
					test = false;
				}
			}
			
			if (test) {
				return coupon;
			}
		}
		
		return null;
	}
	
	public void setCouponDao(SimpleCouponDao couponDao) {
		this.couponDao = couponDao;
	}

	public void setCouponStore(CouponStore couponStore) {
		this.couponStore = couponStore;
	}

}