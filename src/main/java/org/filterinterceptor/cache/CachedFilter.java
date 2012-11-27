package org.filterinterceptor.cache;

/**
 * Class used to store data in Cache
 */
public class CachedFilter {
	private final String filterDescription;
	private final Object filteredService;

	/**
	 * Constructor with fileds
	 * 
	 * @param filterDescription
	 *            the filter description
	 * @param filteredService
	 *            the filtered service to apply
	 */
	public CachedFilter(String filterDescription, Object filteredService) {
		this.filterDescription = filterDescription;
		this.filteredService = filteredService;
	}

	/**
	 * Get the filter description
	 * 
	 * @return the filter description
	 */
	public String getFilterDescription() {
		return filterDescription;
	}

	/**
	 * Get the filtered service
	 * 
	 * @return the filteredService
	 */
	public Object getFilteredService() {
		return filteredService;
	}
}
