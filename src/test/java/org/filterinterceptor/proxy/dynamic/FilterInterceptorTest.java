package org.filterinterceptor.proxy.dynamic;

import java.lang.reflect.Method;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.proxy.dynamic.FilterInterceptor;
import org.filterinterceptor.sample.service.IService;
import org.junit.Test;


import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

public class FilterInterceptorTest {

	@Test
	public void invoke_withNoExtendToInterfaces() throws Throwable {
		invoke(false);
	}

	@Test
	public void invoke_withExtendToInterfaces() throws Throwable {
		invoke(true);
	}

	private void invoke(boolean extendToInterfaces) throws NoSuchMethodException, Throwable {
		FilterService fs = createMock(FilterService.class);
		IService service = createMock(IService.class);

		// expect
		int paramValue = 10;
		Object retValue = new Object();
		Method method = IService.class.getDeclaredMethod("test0", int.class);
		expect(fs.invoke(service, extendToInterfaces, method, paramValue)).andReturn(retValue);

		// replay
		replay(service, fs);

		// test
		FilterInterceptor<IService> fi = new FilterInterceptor<IService>(fs, service, extendToInterfaces);
		Object ret = fi.invoke(service, method, new Object[] { paramValue });

		// check
		verify(service, fs);
		assertNotNull("Result must not be null", ret);
		assertEquals("FilterService must be called", retValue, ret);
	}
}
