package org.filterinterceptor.management;

import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.management.mbean.FilterManagement;
import org.filterinterceptor.management.mbean.FilterServiceManagement;
import org.filterinterceptor.spi.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Register MBeans in default MBean server platform
 */
public class FilterServiceMBeansRegister implements Observer {

	private static final Logger logger = LoggerFactory.getLogger(FilterServiceMBeansRegister.class);

	/**
	 * Default domain used in JMX server
	 */
	public static final String MBEAN_DEFAULT_DOMAIN = "FilterService";
	public static final MBeanServer MBEAN_DEFAULT_SERVER = ManagementFactory.getPlatformMBeanServer();

	private final FilterService filterService;
	private String mBeanDomain = MBEAN_DEFAULT_DOMAIN;
	private MBeanServer mbs = MBEAN_DEFAULT_SERVER;

	/**
	 * Construct Register with default domain value
	 * 
	 * @param filterService
	 *            the filterService to expose
	 */
	public FilterServiceMBeansRegister(FilterService filterService) {
		if (filterService == null)
			throw new IllegalArgumentException("FilterService can't be null");
		this.filterService = filterService;

		filterService.addObserver(this);
	}

	/**
	 * Construct Register with default domain value
	 * 
	 * @param filterService
	 *            the filterService to expose
	 * @param mBeanDomain
	 *            the name of the domain of the MBeans
	 */
	public FilterServiceMBeansRegister(FilterService filterService, String mBeanDomain) {
		this(filterService);
		if (mBeanDomain != null && mBeanDomain.length() != 0)
			this.mBeanDomain = mBeanDomain;
	}

	/**
	 * Publish managements methods in JMX Server
	 */
	public void initMBean() {
		logger.info("Register MBeans in domain: " + mBeanDomain);

		// Register FilterService
		registerFilterServiceMBean(true);

		// Register Filters
		registerFiltersMBean(true);
	}

	/**
	 * Refresh MBeans Filter in JMX Server
	 */
	public void refreshMBean() {
		// Refresh Filters
		registerFiltersMBean(true);
	}

	/**
	 * Unpublish MBeans in JMX Server
	 */
	public void resetMBean() {
		// Unregister FilterService
		registerFilterServiceMBean(false);

		// Unregister Filters
		registerFiltersMBean(false);
	}

	/**
	 * Set the MBeanServer<br>
	 * The default MBeanServer used is the {@link #MBEAN_DEFAULT_DOMAIN}
	 * 
	 * @param mBeanServer
	 *            the MBeanServer used to register the MBeans
	 */
	public void setMBeanServer(MBeanServer mBeanServer) {
		mbs = mBeanServer;
	}

	/*
	 * PRIVATE
	 */
	/**
	 * Register or unregister FilterService MBean
	 * 
	 * @param register
	 *            set to true to register, false to unregister
	 */
	private void registerFilterServiceMBean(boolean register) {

		try {
			ObjectName objectName = new ObjectName(mBeanDomain, "type", "Management");
			if (register) {
				logger.info("Register MBean FilterServiceManagement");
				if (!mbs.isRegistered(objectName)) {
					mbs.registerMBean(new FilterServiceManagement(filterService), objectName);
					logger.debug("MBean FilterServiceManagement registered");
				} else {
					logger.debug("MBean FilterServiceManagement already registered");
				}
			} else {
				logger.info("Unregister MBean FilterServiceManagement");
				if (mbs.isRegistered(objectName)) {
					mbs.unregisterMBean(objectName);
					logger.debug("MBean FilterServiceManagement unregistered");
				} else {
					logger.debug("MBean FilterServiceManagement already unregistered");
				}
			}
		} catch (MalformedObjectNameException e) {
			logger.warn("Bean name is not compliant: " + e.getMessage(), e);
		} catch (InstanceAlreadyExistsException e) {
			logger.warn("Bean already registered: " + e.getMessage(), e);
		} catch (MBeanRegistrationException e) {
			logger.warn("Can't register/unregister bean: " + e.getMessage(), e);
		} catch (NotCompliantMBeanException e) {
			logger.warn("Bean is not compliant: " + e.getMessage(), e);
		} catch (InstanceNotFoundException e) {
			logger.warn("Bean instance not found: " + e.getMessage(), e);
		}
	}

	/**
	 * Register or unregister Filters MBeans
	 * 
	 * @param register
	 *            set to true to register, false to unregister
	 */
	private void registerFiltersMBean(boolean register) {
		for (final Filter<?> filter : filterService.getAllFilters()) {
			try {
				@SuppressWarnings("serial")
				ObjectName objectName = new ObjectName(mBeanDomain, new Hashtable<String, String>() {
					{
						put("filter", filter.getService().getSimpleName());
						put("label", filter.getDescription());
					}
				});

				if (register) {
					logger.info("Register MBean FilterManagement: " + filter.getDescription());
					if (!mbs.isRegistered(objectName)) {
						mbs.registerMBean(new FilterManagement(filter, filterService), objectName);
						logger.debug("MBean FilterManagement registered");
					} else {
						logger.debug("MBean FilterManagement already registered");
					}
				} else {
					logger.info("Unregister MBean FilterManagement: " + filter.getDescription());
					if (mbs.isRegistered(objectName)) {
						mbs.unregisterMBean(objectName);
						logger.debug("MBean FilterManagement unregistered");
					} else {
						logger.debug("MBean FilterManagement already unregistered");
					}
				}
			} catch (MalformedObjectNameException e) {
				logger.warn("Bean name is not compliant: " + e.getMessage(), e);
			} catch (InstanceAlreadyExistsException e) {
				logger.warn("Bean already registered: " + e.getMessage(), e);
			} catch (MBeanRegistrationException e) {
				logger.warn("Can't register bean: " + e.getMessage(), e);
			} catch (NotCompliantMBeanException e) {
				logger.warn("Bean is not compliant: " + e.getMessage(), e);
			} catch (InstanceNotFoundException e) {
				logger.warn("Bean instance not found: " + e.getMessage(), e);
			}
		}
	}

	/*
	 * Observer pattern implementation
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		refreshMBean();
	}

}
