package org.filterinterceptor.management.mbean;

/**
 * This interface is used by JMX API to create MBean
 */
public interface FilterManagementMBean {

	/**
	 * Get the name of the service filtered (RO information)
	 * 
	 * @return a string represents the name of the filtered service
	 */
	String getService();

	/**
	 * Return the description of the filter (RO information)
	 * 
	 * @return the description of the filter
	 */
	String getDescription();

	/**
	 * Change the status of the filter
	 * 
	 * @param active
	 *            the new status to set
	 */
	void setActive(boolean active);

	/**
	 * Return the status of the filter
	 * 
	 * @return the current status
	 */
	boolean isActive();

	/**
	 * Change the priority of the filter
	 * 
	 * @param priority
	 *            the new priority to set
	 */
	void setPriority(int priority);

	/**
	 * Return the priority of the filter
	 * 
	 * @return the current priority
	 */
	int getPriority();
}
