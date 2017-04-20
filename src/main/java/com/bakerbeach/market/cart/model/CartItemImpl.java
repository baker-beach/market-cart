package com.bakerbeach.market.cart.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mongodb.morphia.annotations.Transient;

import com.bakerbeach.market.core.api.model.CartItemQualifier;

public class CartItemImpl extends AbstractCartItem {

	private Map<String, CartItemComponent> components = new TreeMap<String, CartItemComponent>();

	public CartItemImpl() {
		setQualifier(CartItemQualifier.PRODUCT);
	}

	public CartItemImpl(String gtin, BigDecimal quantity) {
		setQualifier(CartItemQualifier.PRODUCT);
		this.gtin = gtin;
		this.quantity = quantity;
	}

	@Override
	public Map<String, CartItemComponent> getComponents() {
		return components;
	}

	public List<CartItemOption> getAllOptions() {
		List<CartItemOption> list = new ArrayList<CartItemOption>();
		for (CartItemComponent component : components.values()) {
			list.addAll(component.getOptions().values());
		}

		return list;
	}

	public static class CartItemComponentImpl implements CartItemComponent {
		private String name;
		private Map<String, CartItemOption> options = new TreeMap<String, CartItemOption>();

		public CartItemComponentImpl() {
		}

		public CartItemComponentImpl(String mame) {
			this.name = mame;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Map<String, CartItemOption> getOptions() {
			return options;
		}

	}

	public static class CartItemOptionImpl implements CartItemOption {
		private String gtin;
		private Integer quantity = 0;
		private BigDecimal unitPrice = BigDecimal.ZERO;
		@Transient
		private BigDecimal totalPrice = BigDecimal.ZERO;
		protected BigDecimal monthlyUnitPrice;
		@Transient
		protected BigDecimal monthlyTotalPrice;
		private String title1;
		private String title2;
		private String title3;
		private Map<String, BigDecimal> unitPrices = new HashMap<String, BigDecimal>();

		public CartItemOptionImpl() {
		}

		public CartItemOptionImpl(String gtin) {
			this.gtin = gtin;
		}

		@Override
		public String getGtin() {
			return gtin;
		}

		public void setGtin(String gtin) {
			this.gtin = gtin;
		}

		@Override
		public Integer getQuantity() {
			return quantity;
		}

		@Override
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		@Override
		public BigDecimal getUnitPrice() {
			return unitPrice;
		}

		@Override
		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}

		public BigDecimal getMonthlyUnitPrice() {
			return monthlyUnitPrice;
		}

		public void setMonthlyUnitPrice(BigDecimal monthlyUnitPrice) {
			this.monthlyUnitPrice = monthlyUnitPrice;
		}

		@Override
		public BigDecimal getTotalPrice() {
			return totalPrice;
		}

		@Override
		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}

		public BigDecimal getMonthlyTotalPrice() {
			return monthlyTotalPrice;
		}

		public void setMonthlyTotalPrice(BigDecimal monthlyTotalPrice) {
			this.monthlyTotalPrice = monthlyTotalPrice;
		}

		@Override
		public String getTitle1() {
			return title1;
		}

		@Override
		public void setTitle1(String title1) {
			this.title1 = title1;
		}

		@Override
		public String getTitle2() {
			return title2;
		}

		@Override
		public void setTitle2(String title2) {
			this.title2 = title2;
		}

		@Override
		public String getTitle3() {
			return title3;
		}

		@Override
		public void setTitle3(String title3) {
			this.title3 = title3;
		}

		public Map<String, BigDecimal> getUnitPrices() {
			return unitPrices;
		}

		public void setUnitPrices(Map<String, BigDecimal> unitPrices) {
			this.unitPrices = unitPrices;
		}
	}

}
