package com.bakerbeach.market.cart.coupon;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bakerbeach.market.cart.api.service.CartService;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.CartItemQualifier;
import com.bakerbeach.market.core.api.model.Coupon;
import com.bakerbeach.market.core.api.model.CouponError;
import com.bakerbeach.market.core.api.model.CouponResult;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.core.api.model.ShopContext;

public class SimpleCouponImpl implements Coupon {
	private static final long serialVersionUID = 1L;

	private String code;
	private BigDecimal rate;
	private BigDecimal shippingDiscountRate;
	private Integer maxIndividualUse;
	private Set<String> emails = new HashSet<String>();
	private Boolean newsletterSubscription = false;
	private Date start;
	private Date end;

	private CouponError error;
	
	@Override
	public Coupon getInstance() {
		SimpleCouponImpl coupon = new SimpleCouponImpl();
		coupon.setCode(this.getCode());
		coupon.setRate(this.getRate());
		coupon.setShippingDiscountRate(this.getShippingDiscountRate());
		coupon.setMaxIndividualUse(maxIndividualUse);
		coupon.setEmails(this.getEmails());
		coupon.setNewsletterSubscription(this.getNewsletterSubscription());
		coupon.setStart(this.getStart());
		coupon.setEnd(this.getEnd());

		return coupon;
	}
	
	@Override
	public CouponResult apply(Map<String, Object> context) {
		try {
			CouponResult couponResult = new CouponResult();
			
			if (context.containsKey("cartService") && context.containsKey("customer") && context.containsKey("cart")) {
				CartService cartService = (CartService) context.get("cartService");
				Customer customer = (Customer) context.get("customer");
				Cart cart = (Cart) context.get("cart");
				
				if (!checkTime(new Date())) {
					CouponError error = new CouponError("coupon.error.timespan", getStart(), getEnd());
					couponResult.getErrors().add(error);
					return couponResult;
				}
				
				if (maxIndividualUse != null) {
					Integer count = cartService.getIndividualUseCount(code, customer.getId());
					if (count >= maxIndividualUse) {
						setError(new CouponError("coupon.error.maxIndividualUse"));
						couponResult.addError(error);
						return couponResult;
					}
				}
				
				BigDecimal discount = BigDecimal.ZERO;
				List<String> qualifiers = Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT);
				for (CartItem item : cart.getCartItems()) {
					if (qualifiers.contains(item.getQualifier())) {
						String id = item.getId();
						// TODO: check if reduced price
						discount = discount.add(item.getTotalPrice().multiply(rate).setScale(2, BigDecimal.ROUND_HALF_DOWN).negate());
					}
				}
				
				couponResult.getDiscounts().put("total", discount);
			}
				
			return couponResult;
		} catch (Exception e) {
			CouponResult couponResult = new CouponResult();
			couponResult.getErrors().add(new CouponError("coupon.error.general", e.getMessage()));
			return couponResult;
		}
	}
	
	@Override
	public CouponResult apply(ShopContext shopContext, Customer customer, Cart cart) {
		try {
			CouponResult couponResult = new CouponResult();
			
			if (!checkTime(new Date())) {
				CouponError error = new CouponError("coupon.error.timespan", getStart(), getEnd());
				couponResult.getErrors().add(error);
				return couponResult;
			}
	
			/*
			if (maxIndividualUse != null) {
				Integer count = cartService.getIndividualUseCount(code, customer.getId());
				if (count >= maxIndividualUse) {
					setError(new CouponError("coupon.error.maxIndividualUse"));
					couponResult.addError(error);
					return couponResult;
				}
			}
			*/

			// ...
			

			// TODO: check if line item relevant
			BigDecimal discount = BigDecimal.ZERO;
			List<String> qualifiers = Arrays.asList(CartItemQualifier.PRODUCT, CartItemQualifier.VPRODUCT);
			for (CartItem item : cart.getCartItems()) {
				if (qualifiers.contains(item.getQualifier())) {
					String id = item.getId();
					// TODO: check if reduced price
					discount = discount.add(item.getTotalPrice().multiply(rate).setScale(2, BigDecimal.ROUND_HALF_DOWN).negate());
				}
			}

			couponResult.getDiscounts().put("total", discount);

			return couponResult;
		} catch (Exception e) {
			CouponResult couponResult = new CouponResult();
			couponResult.getErrors().add(new CouponError("coupon.error.general", e.getMessage()));
			return couponResult;
		}	
	}

//	@Override
//	public CouponServiceResult apply(CouponService couponService, ShopCtx shopContext, Customer customer, Cart cart) {
//		CouponServiceResult couponResult = new SimpleCouponResultImpl();
//		
//		if (!checkTime(new Date())) {
//			setError(new CouponError("coupon.error.timespan", getStart(), getEnd()));
//			couponResult.addError(error);
//			return couponResult;
//		}
//
//		if (maxIndividualUse != null) {
//			Integer count = couponService.getIndividualUseCount(code, customer.getId());
//			if (count >= maxIndividualUse) {
//				setError(new CouponError("coupon.error.maxIndividualUse"));
//				couponResult.addError(error);
//				return couponResult;
//			}
//		}
//
//		if (!emails.isEmpty()) {
//			if (!emails.contains(customer.getEmail())) {
//				setError(new CouponError("coupon.error.whitelist"));
//				couponResult.addError(error);
//				return couponResult;
//			}
//		}
//
//		if (getNewsletterSubscription()) {
//			try {
//				NewsletterSubscription subscription = couponService.getNewsletterService().getSubscription(customer.getEmail());
//				if (subscription == null) {
//					setError(new CouponError("coupon.error.newsletterSubscription"));
//					couponResult.addError(error);
//					return couponResult;
//				}
//			} catch (Exception e) {
//				setError(new CouponError("coupon.error.newsletterSubscription"));
//				couponResult.addError(error);
//				return couponResult;
//			}
//		}
//
//		BigDecimal valueOfGoods = cart.getValueOfGoods();
//		BigDecimal discount = valueOfGoods.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_DOWN);
//		
//		if (shippingDiscountRate != null) {
//			BigDecimal shipping = cart.getShipping();
//			BigDecimal shippingDiscount = shipping.multiply(shippingDiscountRate).setScale(2, BigDecimal.ROUND_HALF_DOWN);
//			discount = discount.add(shippingDiscount);
//		}
//		
//		discount = discount.negate();
//
//		couponResult.setDiscount(discount);
//
//		return couponResult;
//	}
	
	@Override
	public Boolean checkTime(Date date) {
		if (getStart() != null && date.before(getStart())) {
			return false;
		}
		
		if (getEnd() != null && date.after(getEnd())) {
			return false;			
		}
		
		return true;
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
	public BigDecimal getShippingDiscountRate() {
		return shippingDiscountRate;
	}
	
	public void setShippingDiscountRate(BigDecimal shippingDiscountRate) {
		this.shippingDiscountRate = shippingDiscountRate;
	}
	
	@Override
	public Integer getMaxIndividualUse() {
		return maxIndividualUse;
	}
	
	public void setMaxIndividualUse(Integer maxIndividualUse) {
		this.maxIndividualUse = maxIndividualUse;
	}

	public void setEmail(String email) {
		emails.addAll(Arrays.asList(email.split(",")));
	}

	public Set<String> getEmails() {
		return emails;
	}

	public void setEmails(Set<String> emails) {
		this.emails = emails;
	}

	public Boolean getNewsletterSubscription() {
		return newsletterSubscription;
	}

	public void setNewsletterSubscription(Boolean newsletterSubscription) {
		this.newsletterSubscription = newsletterSubscription;
	}
	
	@Override
	public Date getStart() {
		return start;
	}
	
	public void setStart(Date start) {
		this.start = start;
	}
	
	@Override
	public Date getEnd() {
		return end;
	}
	
	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public CouponError getError() {
		return error;
	}

	@Override
	public void setError(CouponError error) {
		this.error = error;
	}

	@Override
	public String getNature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDiscountType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getCartDiscount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getMaxUsesPerCoupon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxUsesPerCoupon(Long maxUsesPerCoupon) {
		// TODO Auto-generated method stub

	}

	@Override
	public Long getMaxUsesPerCustomer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxUsesPerCustomer(Long maxUsesPerCustomer) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getExtProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExtProvider(String extProvider) {
		// TODO Auto-generated method stub

	}

}
