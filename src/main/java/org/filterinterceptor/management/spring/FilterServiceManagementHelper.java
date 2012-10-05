package org.filterinterceptor.management.spring;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.management.FilterServiceManagement;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class initialize FilterServiceManagement.
 */
public class FilterServiceManagementHelper {

	private FilterService filterService;

	/**
	 * Call this init method in spring config file
	 */
	public void initJmx() {
		FilterServiceManagement.initMBean(filterService);
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
}
