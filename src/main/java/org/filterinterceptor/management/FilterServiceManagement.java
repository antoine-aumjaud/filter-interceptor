package org.filterinterceptor.management;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.spi.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class exposes {@link FilterServiceManagement} and {@link FilterManagement} methods in MBean and register MBean components
 */
public class FilterServiceManagement
  implements FilterServiceManagementMBean
{
  /**
   * Default domain used in JMX server
   */
  public static final String MBEAN_DEFAULT_DOMAIN = "FilterService";

  private static final Logger logger = LoggerFactory.getLogger(FilterServiceManagement.class);

  private final FilterService filterService;

  // TODO tests JMX

  @SuppressWarnings("serial")
  /**
   * Publish managements methods in platform JMX Server
   * @param filterService the filterService to expose
   * @param mBeanDomain the name of the domain of the beans
   */
  public static void initMBean(FilterService filterService, String mBeanDomain)
  {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    // MBeanServerFactory.createMBeanServer(filterService.getClass().getName());
    try
    {
      if (mBeanDomain == null || mBeanDomain.length() == 0)
      {
        mBeanDomain = MBEAN_DEFAULT_DOMAIN;
      }
      logger.info("Register MBeans in domain: " + mBeanDomain);

      logger.info("Register MBean FilterServiceManagement");
      mbs.registerMBean(new FilterServiceManagement(filterService), new ObjectName(mBeanDomain, "type", "Management"));

      logger.info("Register MBeans FilterManagement");
      for (final Filter<?> filter : filterService.getAllFilters())
      {
        mbs.registerMBean(new FilterManagement(filter, filterService), new ObjectName(mBeanDomain, new Hashtable<String, String>() {
          {
            put("filter", filter.getService().getSimpleName());
            put("label", filter.getDescription());
          }
        }));
      }
      logger.info("MBeans registered successfully");
    }
    catch (InstanceAlreadyExistsException e)
    {
      // TODOJ7: exception management
      logger.warn("Bean already registered: " + e.getMessage(), e);
    }
    catch (MBeanRegistrationException e)
    {
      logger.warn("Can't register bean: " + e.getMessage(), e);
    }
    catch (NotCompliantMBeanException e)
    {
      logger.warn("Bean is not compliant: " + e.getMessage(), e);
    }
    catch (MalformedObjectNameException e)
    {
      logger.warn("Bean name is not compliant: " + e.getMessage(), e);
    }
  }

  public static void resetMBean(FilterService filterService, String mBeanDomain)
  {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

    try
    {
      ObjectName objectName = new ObjectName(mBeanDomain, "type", "Management");

      if (mbs.isRegistered(objectName))
      {
        try
        {
          logger.info("Unregister MBean FilterServiceManagement");

          mbs.unregisterMBean(objectName);
        }
        catch (InstanceNotFoundException e)
        {
          logger.warn("Bean instance not found: " + e.getMessage(), e);
        }
        catch (MBeanRegistrationException e)
        {
          logger.warn("Can't unregister bean: " + e.getMessage(), e);
        }
      }

      logger.info("Unregister MBeans FilterManagement");
      for (final Filter<?> filter : filterService.getAllFilters())
      {
        try
        {
          mbs.unregisterMBean(new ObjectName(mBeanDomain, new Hashtable<String, String>() {
            {
              put("filter", filter.getService().getSimpleName());
              put("label", filter.getDescription());
            }
          }));
        }
        catch (InstanceNotFoundException e)
        {
          logger.warn("Bean instance not found: " + e.getMessage(), e);
        }
        catch (MBeanRegistrationException e)
        {
          logger.warn("Can't unregister bean: " + e.getMessage(), e);
        }
      }
      logger.info("MBeans unregister successfully");
    }
    catch (MalformedObjectNameException e)
    {
      logger.warn("Bean name is not compliant: " + e.getMessage(), e);
    }
  }

  /*
   * MBEAN methods implementation
   */
  @Override
  public List<String> getFilters()
  {
    List<String> ret = new ArrayList<String>();
    for (Filter<?> filter : filterService.getAllFilters())
    {
      ret.add(filter.toString());
    }
    return ret;
  }

  @Override
  public List<String> getActiveFilters()
  {
    List<String> ret = new ArrayList<String>();
    for (Map.Entry<String, Filter<?>> filterMap : filterService.getAllActiveFiltersUsed().entrySet())
    {
      ret.add(String.format("%s: %s", filterMap.getKey(), filterMap.getValue().toString()));
    }
    return ret;
  }

  @Override
  public void reinitFilters() throws IOException
  {
    filterService.initFilters();
  }

  /*
   * PRIVATE
   */
  private FilterServiceManagement(FilterService filterService)
  {
    this.filterService = filterService;
  }
}
