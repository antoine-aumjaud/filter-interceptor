package org.filterinterceptor.spi;

import org.filterinterceptor.FilterService;

/**
 * This class represents a filter to apply to a service
 * 
 * @param <T>
 *            The type of the service to filter
 */
public abstract class Filter<T> implements Comparable<Filter<T>> {

	private final String description;
	private int priority;
	private boolean active = true;

	/**
	 * The default constructor
	 * 
	 * @param description
	 *            the description of the filter
	 * @param priority
	 *            the priority of the filter
	 * @throws NullPointerException
	 *             if description is null
	 */
	protected Filter(String description, int priority) {
		if (description == null)
			throw new NullPointerException("Filter description is mandatory");

		this.description = description;
		this.priority = priority;
	}

	/**
	 * Return the description of the filter
	 * 
	 * @return the description of the filter
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Return the priority of the filter
	 * 
	 * @return the priority of the filter
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Change the priority of the filter Beware: use
	 * {@link FilterService#setFilterPriority(Filter, int)} method to update the
	 * filter status, otherwise, the active filters list must not be recomputed
	 * 
	 * @param priority
	 *            the new priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * Get the active status of the filter
	 * 
	 * @return the active status of the filter
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the active status of the filter Beware: use
	 * {@link FilterService#setFilterActiveStatus(Filter, boolean)} method to
	 * update the filter status, otherwise, the active filters list must not be
	 * recomputed
	 * 
	 * @param active
	 *            the value to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return String.format("Service: %s, active: %b, description=%s, priority=%d", getService().getSimpleName(),
				active, description, priority);
	}

	@Override
	public final int hashCode() {
		return description.hashCode();
	}

	@Override
	public final boolean equals(Object other) {
		// warning: compare not class name not class with operator "==", they
		// are not load in the same classloader
		if (other != null && other instanceof Filter & other.getClass().getName().equals(getClass().getName())) {
			Filter<?> otherFilter = (Filter<?>) other;
			return description.equals(otherFilter.description);
		}
		return false;
	}

	@Override
	public final int compareTo(Filter<T> other) {
		if (other == null)
			throw new NullPointerException("Other filter to compare is null");

		int ret = description.compareTo(other.description);
		return (ret == 0) ?
		// TODOJ7: Integer.compare(getPriority(), other.getPriority())
		((Integer) getPriority()).compareTo(other.getPriority())
				: ret;
	};

	/*
	 * ABSTRACT
	 */

	/**
	 * Return the service of the filter
	 * 
	 * @return the service of the filter
	 */
	public abstract Class<? extends T> getService();

	/**
	 * Return the new implementation of the service
	 * 
	 * @param service
	 *            the filtered service
	 * @return the new implementation of the service
	 */
	public abstract T getFilterServiceImpl(T service);
}
