package org.filterinterceptor.management.spring;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.management.FilterServiceManagement;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class initialize FilterServiceManagement.
 */
public class FilterServiceManagementHelper {

	private FilterService filterService;
	private String mBeanDomain;

	/**
	 * Call this init method in spring config file
	 */
	public void initJmx() {
		FilterServiceManagement.initMBean(filterService, mBeanDomain);
	}

	/*
	 * BEAN
	 */
	/**
	 * Set the filter service use to manage filters
	 * 
	 * @param filterService
	 *            the service filter to use
	 */
	@Required
	public void setFilterService(FilterService filterService) {
		this.filterService = filterService;
	}

	/**
	 * Set the domain name, can be used if several filter service are exposed in
	 * the same JVM
	 * 
	 * @param domain
	 *            the domain name to use. Can be null, the default value is
	 *            {@link FilterServiceManagement#MBEAN_DEFAULT_DOMAIN}
	 */
	// @Required //optional
	public void setDomain(String domain) {
		this.mBeanDomain = domain;
	}
}
