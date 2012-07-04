package org.filterinterceptor.management;

import java.io.IOException;
import java.util.List;

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

}
