package com.bakerbeach.market.cart.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.annotations.Version;

import com.bakerbeach.market.cart.api.model.CartRuleMessage;
import com.bakerbeach.market.cart.api.service.CartRulesAware;
import com.bakerbeach.market.core.api.model.Cart;
import com.bakerbeach.market.core.api.model.CartItem;
import com.bakerbeach.market.core.api.model.Coupon;
import com.bakerbeach.market.core.api.model.Total;

@Entity(noClassnameStored = false)
public class XCartImpl implements Cart, CartRulesAware {

	@Id
	protected ObjectId id;
	@Property("shop_code")
	protected String shopCode;
	@Property("customer_id")
	protected String customerId;
	@Property("client_id")
	private String clientId;
	protected String status = "ACTIVE";
	protected String name;
	@Embedded
	protected Map<String, CartItem> items = new LinkedHashMap<>();
	@Transient
	protected Total total;
	@Transient
	protected Total discount;
	@Transient
	protected BigDecimal valueOfGoods;
	@Transient
	protected BigDecimal valueOfShippingGoods;
	@Transient
	protected BigDecimal shipping;
	@Transient
	protected BigDecimal payment;
	@Property("created_at")
	protected Date createdAt;
	@Property("created_by")
	protected String createdBy;
	@Property("updated_at")
	protected Date updatedAt;
	@Property("updated_by")
	protected String updatedBy;
	@Property("workflow")
	protected String workflow;
	protected Map<String, Object> attributes = new HashMap<String, Object>();
	
	@Transient
	protected Map<String, CartRuleMessage> cartRuleMessages = new HashMap<>();
	@Transient
	protected Set<String> couponRules = new LinkedHashSet<>();
	@Transient
	protected Set<String> commonRules = new LinkedHashSet<>();

	protected String foo = "bar";

	public XCartImpl() {
	}

	public XCartImpl(String shopCode) {
		this.shopCode = shopCode;
	}
	
	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	@Version
	private Long version;

	@Override
	public void clear() {
		valueOfGoods = null;
		valueOfShippingGoods = null;
		shipping = null;
		payment = null;
		total = null;
		couponRules = new LinkedHashSet<String>(1);
		commonRules = new LinkedHashSet<String>(1);
		items.clear();
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
	public String getShopCode() {
		return shopCode;
	}

	@Override
	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	@Override
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Override
	public String getCustomerId() {
		return customerId;
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
	public Map<String, CartItem> getItems() {
		return items;
	}

	@Override
	public CartItem getNewItem(String code, BigDecimal quantity) {
		XCartItemImpl item = new XCartItemImpl();
		item.setCode(code);
		item.setQuantity(quantity);

		return item;
	}

	@Override
	@Deprecated
	public List<CartItem> getCartItems() {
		return new ArrayList<CartItem>(items.values());
	}

	@Override
	public CartItem findItemById(String id) {
		return items.get(id);
	}

	@Override
	public List<CartItem> findItemsByQualifier(String... qualifiers) {
		return findItemsByQualifier(Arrays.asList(qualifiers));
	}

	public List<CartItem> findItemsByQualifier(List<String> qualifiers) {
		return items.values().stream().filter(item -> qualifiers.contains(item.getQualifier()))
				.collect(Collectors.toList());
	}

	@Override
	public boolean addAll(Collection<CartItem> items) {
		return items.addAll(items);
	}

	@Override
	public boolean add(CartItem item) {
		if (BigDecimal.ZERO.compareTo(item.getQuantity()) == -1) {
			items.put(item.getId(), item);			
			return true;
		}
		
		return false;
	}

	@Override
	public void set(CartItem item) {
		items.put(item.getId(), item);
	}

	@Override
	public boolean remove(CartItem item) {
		items.remove(item.getId());
		return true;
	}

	@Override
	public boolean remove(String id) {
		items.remove(id);
		return true;
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Deprecated
	@Override
	public List<Coupon> getCoupons() {
		return null;
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
	public Total getDiscount() {
		return discount;
	}

	@Override
	public void setDiscount(Total discount) {
		this.discount = discount;
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
	public BigDecimal getPayment() {
		return payment;
	}

	@Override
	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}

	@Override
	public BigDecimal getGrandTotal() {
		return total.getGross();
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

	@Override
	public String getWorkflow() {
		return workflow;
	}

	@Override
	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof XCartImpl) {
			return this.getId().equals(((XCartImpl) other).getId());
		}
		return false;
	}
	
	@Override
	public Set<String> getCouponRules() {
		return couponRules;
	}
	
	@Override
	public Set<String> getCommonRules() {
		return commonRules;
	}
	
	@Override
	public Map<String, CartRuleMessage> getCartRuleMessages() {
		return cartRuleMessages;
	}

}
