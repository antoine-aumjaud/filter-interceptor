package org.filterinterceptor.management.mbean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.spi.Filter;

/**
 * This class exposes {@link FilterServiceManagement} methods in MBean
 */
public class FilterServiceManagement implements FilterServiceManagementMBean {

	private final FilterService filterService;

	public FilterServiceManagement(FilterService filterService) {
		this.filterService = filterService;
	}

	/*
	 * MBEAN methods implementation
	 */
	@Override
	public List<String> getFilters() {
		List<String> ret = new ArrayList<String>();
		for (Filter<?> filter : filterService.getAllFilters())
			ret.add(filter.toString());
		return ret;
	}

	@Override
	public List<String> getActiveFilters() {
		List<String> ret = new ArrayList<String>();
		for (Map.Entry<String, Filter<?>> filterMap : filterService.getAllActiveFiltersUsed().entrySet())
			ret.add(String.format("%s: %s", filterMap.getKey(), filterMap.getValue().toString()));
		return ret;
	}

	@Override
	public void reinitFilters() throws IOException {
		filterService.initFilters();
	}

	/*
	 * Implementation for test (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		FilterServiceManagement fs2 = (FilterServiceManagement) obj;
		return fs2.filterService == filterService;
	}

	/*
	 * Implementation to remove warning (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		assert false;
		return 42;
	}

}
