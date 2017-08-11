package com.bakerbeach.market.cart.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.bakerbeach.market.core.api.model.Address;
import com.bakerbeach.market.core.api.model.Currency;
import com.bakerbeach.market.core.api.model.Customer;
import com.bakerbeach.market.core.api.model.FilterList;
import com.bakerbeach.market.core.api.model.ShopContext;

public class TestShopContext implements ShopContext {
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
	public void setData(Map<String, Object> data) {
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
	public Boolean isCurrencySymbolAtFront() {
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
	public Map<String, Object> getSessionData() {
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
	public Map<String, Object> getData() {
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
		return Locale.GERMANY;
	}
	
	@Override
	public Currency getCurrentCurrency() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getCurrencySymbol() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getCurrency() {
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
		return "DE";
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
	public String getApplicationPath() {
		// TODO Auto-generated method stub
		return null;
	}
}
