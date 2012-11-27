package org.filterinterceptor.management.mbean;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * This interface is used by JMX API to create MBean
 */
public interface FilterServiceManagementMBean {

	/**
	 * Return all registered filters
	 * 
	 * @return a list of known filters
	 */
	List<String> getFilters();

	/**
	 * Return all active with higher priority filter by service
	 * 
	 * @return a list of used filters
	 */
	List<String> getActiveFilters();

	/**
	 * Launch the reinitialization of the filter list (relaod form disk)
	 * 
	 * @throws IOException
	 *             if an error while reading the file system
	 */
	void reinitFilters() throws IOException;

	/**
	 * Get the status of filtered method cache
	 * 
	 * @return true if cache is activated
	 */
	boolean isFilteredMethodCacheActive();

	/**
	 * Set the status of filtered method cache
	 * 
	 * @param isActive
	 *            set to true to active the cache
	 */
	void setFilteredMethodCacheActive(boolean isActive);

	/**
	 * Clear the filtered method cache
	 */
	void clearFilteredMethodCache();

	/**
	 * Get keys of the filtered method cache
	 * 
	 * @return a set of keys
	 */
	Set<String> getFilteredMethodCacheKeys();
}
