package org.filterinterceptor.management.mbean;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.spi.Filter;

/**
 * This class exposes {@link Filter} methods in a MBean
 */
public class FilterManagement implements FilterManagementMBean {

	private final Filter<?> filter;
	private final FilterService filterService;

	public FilterManagement(Filter<?> filter, FilterService filterService) {
		this.filter = filter;
		this.filterService = filterService;
	}

	/*
	 * MBEAN methods implementation
	 */

	@Override
	public String getDescription() {
		return filter.getDescription();
	}

	@Override
	public int getPriority() {
		return filter.getPriority();
	}

	@Override
	public void setPriority(int priority) {
		filterService.setFilterPriority(filter, priority);
	}

	@Override
	public boolean isActive() {
		return filter.isActive();
	}

	@Override
	public void setActive(boolean active) {
		filterService.setFilterActiveStatus(filter, active);
	}

	@Override
	public String getService() {
		return filter.getService().getSimpleName();
	}

}
