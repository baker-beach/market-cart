package com.bakerbeach.market.cart.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.annotations.Version;

import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.Coupon;
import com.bakerbeach.market.core.api.model.Total;

@Entity(value="carts", noClassnameStored=true)
public class SimpleCartImpl implements Cart {

	@Id protected ObjectId id;
	@Property("customer_id") protected String customerId;
	@Property("client_id") private String clientId;
	protected String status = "ACTIVE";
	protected String name;
	@Embedded protected List<CartItem> cartItems = new ArrayList<CartItem>();
	@Transient protected List<Coupon> coupons = new ArrayList<Coupon>(1);

	@Transient protected Total total;
	@Transient protected Total discount;
	
	@Transient protected BigDecimal valueOfGoods;
	@Transient protected BigDecimal valueOfShippingGoods;
	@Transient protected BigDecimal shipping;
	@Transient protected BigDecimal payment;
	
	@Property("created_at") protected Date createdAt;
	@Property("created_by") protected String createdBy;
	@Property("updated_at") protected Date updatedAt;
	@Property("updated_by") protected String updatedBy;
	
	@Property("workflow") protected String workflow;
		
	@Version
	private Long version;

	@PrePersist
	protected void trackUpdate() {
		Date now = new Date();
		updatedAt = now;
		if (createdAt == null) {
			createdAt = now;
		}
	}

	@Override
	public boolean add(CartItem item) {
		return cartItems.add(item);
	}
	
	@Override
	public boolean addAll(Collection<CartItem> items) {
		return cartItems.addAll(items);
	}

	@Override
	public List<CartItem> getCartItems() {
		return cartItems;
	}

	@Override
	public boolean remove(CartItem item) {
		return cartItems.remove(item);
	}

	@Override
	public void clear() {
		valueOfGoods = null;
		valueOfShippingGoods = null;
		shipping = null;
		payment = null;
		
		total = null;
		coupons = new ArrayList<Coupon>(1);
		
		cartItems.clear();
	}

	@Override
	public boolean isEmpty() {
		return cartItems.isEmpty();
	}

	@Override
	public CartItem findItemById(String id) {
		if (id != null) {
			for (CartItem item : cartItems) {
				if (item.getId().equals(id)) {
					return item;
				}
			}
		}
		
		return null;
	}

	@Override
	public List<CartItem> findItemsByQualifier(String... qualifiers) {
		return findItemsByQualifier(Arrays.asList(qualifiers));
	}

	@Override
	public List<CartItem> findItemsByQualifier(List<String> qualifiers) {
		LinkedList<CartItem> list = new LinkedList<CartItem>();
		for (CartItem item : cartItems) {			
			if (qualifiers.contains(item.getQualifier())) {
				list.add(item);
			}
		}
		return list;
	}

	@Override
	public String getId() {
		if (id != null) {
			return id.toString();			
		}
		return null;
	}
	
	@Override
	public void setId(String id) {
		if (id != null) {
			this.id = new ObjectId(id);			
		}
	}
	
	@Override
	public String getCustomerId() {
		return customerId;
	}
	
	@Override
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@Override
	public String getClientId() {
		return clientId;
	}
	
	@Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	@Override
	public List<Coupon> getCoupons() {
		return coupons;
	}

	@Override
	public BigDecimal getValueOfGoods() {
		return valueOfGoods;
	}

	@Override
	public void setValueOfGoods(BigDecimal valueOfGoods) {
		this.valueOfGoods = valueOfGoods;
	}

	@Override
	public BigDecimal getValueOfShippingGoods() {
		return valueOfShippingGoods;
	}
	
	@Override
	public void setValueOfShippingGoods(BigDecimal valueOfShippingGoods) {
		this.valueOfShippingGoods = valueOfShippingGoods;
	}

	@Override
	public BigDecimal getShipping() {
		return shipping;
	}

	@Override
	public void setShipping(BigDecimal shipping) {
		this.shipping = shipping;
	}

	@Override
	public Total getTotal() {
		return total;
	}

	@Override
	public void setTotal(Total total) {
		this.total = total;
	}

	@Override
	public BigDecimal getGrandTotal() {
		return total.getGross();
	}

	@Override
	public BigDecimal getPayment() {
		return payment;
	}
	
	@Override
	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}

	@Override
	public Total getDiscount() {
		return discount;
	}
	
	@Override
	public void setDiscount(Total discount) {
		this.discount = discount;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getCreatedAt() {
		return createdAt;
	}

	@Override
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public Date getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String getUpdatedBy() {
		return updatedBy;
	}

	@Override
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public Long getVersion() {
		return version;
	}
	
	public void setVersion(Long version) {
		this.version = version;
	}
	
	@Override
	public String getWorkflow() {
		return workflow;
	}
	
	@Override
	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof SimpleCartImpl) {
			return this.getId().equals(((SimpleCartImpl) other).getId());
		}
		return false;
	}

}
