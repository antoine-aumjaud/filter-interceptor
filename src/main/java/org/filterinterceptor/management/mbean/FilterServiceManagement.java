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
}
