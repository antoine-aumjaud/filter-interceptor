package org.filterinterceptor.proxy.spring;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.filterinterceptor.FilterService;
import org.filterinterceptor.sample.service.DtoSample2;
import org.filterinterceptor.sample.service.IService;
import org.filterinterceptor.sample.service.ServiceImpl;
import org.filterinterceptor.spi.Filter;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
		FilterInterceptor fi = new FilterInterceptor();
		fi.setFilterService(fs);
		fi.setExtendToInterfaces(extendToInterfaces);
		MethodInvocation mi = new TestMethodInvocation(service, method, new Object[] { paramValue });
		Object ret = fi.invoke(mi);

		// check
		verify(service, fs);
		assertNotNull("Result must not be null", ret);
		assertEquals("FilterService must be called", retValue, ret);
	}

	@Test
	public void springSupport() {

		//TODOJ7: change try
		ClassPathXmlApplicationContext context = null;
		// try (ClosableClassPathXmlApplicationContext context = new ClosableClassPathXmlApplicationContext("org/filterinterceptor/proxy/spring/spring-config.xml");) {
		try {
			context = new ClassPathXmlApplicationContext("org/filterinterceptor/proxy/spring/spring-config.xml");

			IService service = (IService) context.getBean("service");
			IService realService = (IService) context.getBean("realService");
			FilterService filterService = (FilterService) context.getBean("filterService");

			String paramValue = "correctParam";
			DtoSample2 param = new DtoSample2(0, null, paramValue, null);

			// test controle
			DtoSample2 ret = realService.test2(param);
			// check
			assertTrue("References must be equals", param == ret);
			assertSame("C property must be unchanged: real service called", paramValue, ret.getC());

			// test on proxy
			ret = service.test2(param);
			// check
			assertTrue("References must be equals", param == ret);
			assertNotSame("C property must be reassigned to a new value: filter called", paramValue, ret.getC());

			// unactivate the filter
			Filter<?> filter = filterService.getActiveFilter(ServiceImpl.class, "test2");
			filterService.setFilterActiveStatus(filter, false);

			// test on proxy
			param.setC(paramValue);
			ret = service.test2(param);
			// check
			assertTrue("References must be equals", param == ret);
			assertSame("C property must be unchanged: filter is unactivate", paramValue, ret.getC());
		} finally { //TODOJ7: remove
			if (context != null)
				context.close();
		}
	}

	/*
	 * Usefull for test
	 */
	class TestMethodInvocation implements MethodInvocation {
		private final Object service;
		private final Method method;
		private final Object[] params;

		TestMethodInvocation(Object service, Method method, Object[] params) {
			this.service = service;
			this.method = method;
			this.params = params;
		}

		@Override
		public Object[] getArguments() {
			return params;
		}

		@Override
		public AccessibleObject getStaticPart() {
			return null;
		}

		@Override
		public Object getThis() {
			return service;
		}

		@Override
		public Object proceed() throws Throwable {
			return method.invoke(service, params);
		}

		@Override
		public Method getMethod() {
			return method;
		}
	}

	/**
	 * A new {@link ClassPathXmlApplicationContext} which implements
	 * {@link AutoCloseable}
	 */
	//TODOJ7: reactivate this code 
	/*class ClosableClassPathXmlApplicationContext extends ClassPathXmlApplicationContext implements AutoCloseable {

		public ClosableClassPathXmlApplicationContext(String configLocation) throws BeansException {
			super(configLocation);
		}
	}*/
}
