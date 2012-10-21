package org.filterinterceptor.management;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observer;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import junit.framework.Assert;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.management.mbean.FilterManagement;
import org.filterinterceptor.management.mbean.FilterServiceManagement;
import org.filterinterceptor.sample.spi.Test1Filter;
import org.filterinterceptor.spi.Filter;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class FilterServiceMBeansRegisterTest {

	@SuppressWarnings("serial")
	@Test
	public void initMBean_testFirstCall() throws MalformedObjectNameException, InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException {

		FilterService fs = createMock(FilterService.class);
		MBeanServer mbs = createMock(MBeanServer.class);

		// expect
		fs.addObserver((Observer) anyObject());
		// Can't define an expect with a register :( The "fs" needs the Observer
		// and the Observer needs the fs...
		expectLastCall();
		ObjectName objectName = new ObjectName(FilterServiceMBeansRegister.MBEAN_DEFAULT_DOMAIN, "type", "Management");
		expect(mbs.isRegistered(objectName)).andReturn(Boolean.FALSE);
		expect(mbs.registerMBean(new FilterServiceManagement(fs), objectName)).andReturn(null);
		final Filter<?> f = new Test1Filter();
		objectName = new ObjectName(FilterServiceMBeansRegister.MBEAN_DEFAULT_DOMAIN, new Hashtable<String, String>() {
			{
				put("filter", f.getService().getSimpleName());
				put("label", f.getDescription());
			}
		});
		expect(fs.getAllFilters()).andReturn(new ArrayList<Filter<?>>() {
			{
				add(f);
				add(f);
			}
		});
		expect(mbs.isRegistered(objectName)).andReturn(Boolean.FALSE).times(2);
		expect(mbs.registerMBean(new FilterManagement(f, fs), objectName)).andReturn(null).times(2);

		// replay
		replay(fs, mbs);

		// test
		FilterServiceMBeansRegister register = new FilterServiceMBeansRegister(fs);
		register.setMBeanServer(mbs);
		register.initMBean();

		// check
		verify(fs, mbs);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void resetMBean() throws MalformedObjectNameException, InstanceNotFoundException, IntrospectionException,
			ReflectionException {
		FilterService fs = createMock(FilterService.class);

		// expect
		fs.addObserver((Observer) anyObject());
		// Can't define an expect with a register :( The "fs" needs the Observer
		// and the Observer needs the fs...
		expectLastCall();
		ObjectName objectName = new ObjectName(FilterServiceMBeansRegister.MBEAN_DEFAULT_DOMAIN, "type", "Management");
		expect(fs.getAllFilters()).andReturn(new ArrayList<Filter<?>>()).times(2);

		// replay
		replay(fs);

		// test
		FilterServiceMBeansRegister register = new FilterServiceMBeansRegister(fs);
		register.initMBean();
		register.resetMBean();

		// check
		verify(fs);
		FilterServiceMBeansRegister.MBEAN_DEFAULT_SERVER.getObjectInstance(objectName);
		Assert.fail("Previous line must throw an exception, been FilterServiceManagement must have removed");
	}
}