package org.filterinterceptor.proxy.dynamic;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.proxy.dynamic.ServiceProxyFactory;
import org.filterinterceptor.sample.service.IService;
import org.junit.Test;


import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

public class ServiceProxyFactoryTest {

	@Test(expected = IllegalStateException.class)
	public void createProxy_withoutFilterService_throwException() {
		ServiceProxyFactory proxyFactory = new ServiceProxyFactory(null);

		// test
		proxyFactory.createProxy(null, false);

		// check
		fail("Create proxy with no FilterService defined must throw an IllegalStateException");
	}

	@Test(expected = NullPointerException.class)
	public void createProxy_withoutService_throwException() {
		ServiceProxyFactory proxyFactory = new ServiceProxyFactory(new FilterService("FAKE"));

		// test
		proxyFactory.createProxy(null, false);

		// check
		fail("Create proxy with no service defined must throw an NPE");
	}

	@Test
	public void createProxy() {
		FilterService fs = createMock(FilterService.class);
		IService service = createMock(IService.class);

		// expect

		// replay
		replay(service, fs);

		// test
		ServiceProxyFactory proxyFactory = new ServiceProxyFactory(fs);
		IService proxyService = proxyFactory.createProxy(service, false);
		assertNotNull("Proxy must not be null", proxyService);

		// verify
		verify(fs, service);
	}

	@Test
	public void invoke_withNoExtendToInterfaces() throws NoSuchMethodException, SecurityException, Throwable {
		invoke(false);
	}

	@Test
	public void invoke_withExtendToInterfaces() throws NoSuchMethodException, SecurityException, Throwable {
		invoke(true);
	}

	private void invoke(boolean extendToInterfaces) throws Throwable, NoSuchMethodException {
		FilterService fs = createMock(FilterService.class);
		IService service = createMock(IService.class);

		// expect
		int paramValue = 1;
		int retValue = 2;
		expect(
				fs.invoke(anyObject(), eq(extendToInterfaces),
						eq(IService.class.getDeclaredMethod("test0", int.class)), eq(paramValue))).andReturn(retValue);

		// replay
		replay(service, fs);

		// test
		ServiceProxyFactory proxyFactory = new ServiceProxyFactory(fs);
		IService proxyService = proxyFactory.createProxy(service, extendToInterfaces);
		int ret = proxyService.test0(1);

		// check
		assertNotNull("Result of proxy must not be null", ret);
		assertEquals("Result of proxy must be equals to mock return value", retValue, ret);

		// verify
		verify(fs, service);
	}
}
