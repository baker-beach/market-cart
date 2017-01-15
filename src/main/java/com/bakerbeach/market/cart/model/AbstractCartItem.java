package com.bakerbeach.market.cart.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.mongodb.morphia.annotations.Transient;

import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.TaxCode;

public abstract class AbstractCartItem implements CartItem{
	protected String id;
	protected String qualifier;
	protected String gtin;
	protected String name;
	protected String brand;
	protected String supplier;
	protected BigDecimal quantity = BigDecimal.ZERO;
	protected BigDecimal discount = BigDecimal.ZERO;
	protected Boolean isVisible = false;
	protected Boolean isVolatile = false;
	protected Boolean isImmutable = false;
	protected BigDecimal unitPrice;
	@Transient protected BigDecimal totalPrice;
	protected BigDecimal monthlyUnitPrice;
	@Transient protected BigDecimal monthlyTotalPrice;
	protected TaxCode taxCode;
	protected BigDecimal taxPercent;
	protected String title1;
	protected String title2;
	protected String title3;
	protected String imageUrl1;
	protected String imageUrl2;
	protected String size;
	protected String color;
	@Deprecated
	protected Map<String, BigDecimal> unitPrices = new HashMap<String, BigDecimal>();
	protected Map<String, Object> attributes = new HashMap<String, Object>();
	
	@Override
	public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("CartItem {");
        builder.append("id: ").append(id).append(",");
        builder.append("gtin: ").append(gtin).append(",");
        builder.append("brand: ").append(brand).append(",");
        builder.append("quantity: ").append(quantity).append(",");
        builder.append("}");

        return builder.toString();
	}

	@Override
	public Map<String,Object> getAttributes() {
		return attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getQualifier() {
		return qualifier;
	}
	
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getGtin() {
		return gtin;
	}
	
	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public String getSupplier() {
		return supplier;
	}
	
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public Boolean isVolatile() {
		return isVolatile;
	}

	public Boolean isVisible() {
		return isVisible;
	}

	public Boolean isImmutable() {
		return isImmutable;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getMonthlyUnitPrice() {
		return monthlyUnitPrice;
	}
	
	public void setMonthlyUnitPrice(BigDecimal monthlyUnitPrice) {
		this.monthlyUnitPrice = monthlyUnitPrice;
	}
	
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public BigDecimal getMonthlyTotalPrice() {
		return monthlyTotalPrice;
	}
	
	public void setMonthlyTotalPrice(BigDecimal monthlyTotalPrice) {
		this.monthlyTotalPrice = monthlyTotalPrice;
	}

	public TaxCode getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(TaxCode taxCode) {
		this.taxCode = taxCode;
	}

	public BigDecimal getTaxPercent() {
		return taxPercent;
	}

	public void setTaxPercent(BigDecimal taxPercent) {
		this.taxPercent = taxPercent;
	}

	public String getTitle1() {
		return title1;
	}

	public void setTitle1(String title1) {
		this.title1 = title1;
	}

	public String getTitle2() {
		return title2;
	}

	public void setTitle2(String title2) {
		this.title2 = title2;
	}

	public String getTitle3() {
		return title3;
	}

	public void setTitle3(String title3) {
		this.title3 = title3;
	}

	public String getImageUrl1() {
		return imageUrl1;
	}
	
	public void setImageUrl1(String imageUrl1) {
		this.imageUrl1 = imageUrl1;
	}
	
	public String getImageUrl2() {
		return imageUrl2;
	}
	
	public void setImageUrl2(String imageUrl2) {
		this.imageUrl2 = imageUrl2;
	}
	
	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}

	public Map<String, BigDecimal> getUnitPrices() {
		return unitPrices;
	}

	public void setUnitPrices(Map<String, BigDecimal> unitPrices) {
		this.unitPrices = unitPrices;
	}
	
}
