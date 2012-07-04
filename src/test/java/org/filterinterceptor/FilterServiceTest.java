package org.filterinterceptor;

import java.io.IOException;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.sample.service.DtoSample1;
import org.filterinterceptor.sample.service.DtoSample2;
import org.filterinterceptor.sample.service.DtoSample3;
import org.filterinterceptor.sample.service.IService;
import org.filterinterceptor.sample.service.ServiceImpl;
import org.filterinterceptor.spi.Filter;
import org.filterinterceptor.spi.FilteredMethod;
import org.junit.Test;


import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

public class FilterServiceTest {
	@Test(expected = IOException.class)
	public void initFilter_wrongFolder_throwIOException() throws IOException {

		// test
		FilterService fs = new FilterService("./src/test/resources/ARG");
		fs.initFilters();

		// check
		fail("IOException must be thrown");
	}

	@Test
	public void initFilter_load() throws IOException {

		// test
		FilterService fs = new FilterService("./src/test/resources/others_filters");
		fs.initFilters();

		// check
		// Check load all filters
		assertEquals("Nb of filters found must be equals to 4", 4, fs.getAllFilters().size());
		// Check active filter override
		assertEquals("Nb of active filter must be equals to 3", 3, fs.getAllActiveFiltersUsed().size());
		Filter<?> filter = fs.getActiveFilter(ServiceImpl.class, "test1");
		// Check multiple filters on the same service method (only the higher
		// priority is active)
		assertNotNull("Filter for ServiceImpl.test1 must be defined", filter);
		assertEquals("Filter for ServiceImpl.test1 must have priority 2", 2, filter.getPriority());
	}

	@Test
	public void initFilter_reload() throws IOException {

		// test
		FilterService fs = new FilterService("./src/test/resources/others_filters");
		fs.initFilters();
		fs.initFilters();

		// check
		assertEquals("Same filters must be load only one time", 4, fs.getAllFilters().size());
	}

	@Test
	public void invoke_exception_callRealService() throws Throwable {

		IService service = createMock(IService.class);

		// expect
		int paramValue = 10;
		int retValue = 11;
		expect(service.test0(paramValue)).andReturn(retValue);

		// replay
		replay(service);

		// test
		FilterService fs = new FilterService(null) {
			@Override
			public Filter<?> getActiveFilter(Class<?> serviceClass, String methodName) {
				throw new NullPointerException();
			}
		};
		Object ret = fs.invoke(service, false, IService.class.getDeclaredMethod("test0", int.class),
				new Object[] { paramValue });

		// check
		verify(service);
		assertNotNull("Result must not be null", ret);
		assertEquals("Real (mocked) Service must be called", retValue, ret);
	}

	@Test
	public void invoke_realService() throws Throwable {

		IService service = createMock(IService.class);

		// expect
		int paramValue = 10;
		int retValue = 11;
		expect(service.test0(paramValue)).andReturn(retValue);

		// replay
		replay(service);

		// test
		FilterService fs = new FilterService(null) {
			@Override
			public Filter<?> getActiveFilter(Class<?> serviceClass, String methodName) {
				return null;
			}
		};
		Object ret = fs.invoke(service, false, IService.class.getDeclaredMethod("test0", int.class),
				new Object[] { paramValue });

		// check
		verify(service);
		assertNotNull("Result must not be null", ret);
		assertEquals("Real (mocked) Service must be called", retValue, ret);
	}

	@Test
	public void invoke_newTreatment() throws Throwable {

		IService service = createMock(IService.class);

		// expect
		int paramValue = 10;

		// replay
		replay(service);

		// test
		FilterService fs = new FilterService(null) {
			@Override
			public Filter<?> getActiveFilter(java.lang.Class<?> serviceClass, String methodName) {
				return new ServiceFilterChangeTreatment();
			};
		};
		Object ret = fs.invoke(service, false, IService.class.getDeclaredMethod("test0", int.class),
				new Object[] { paramValue });

		// check
		verify(service);
		assertNotNull("Result must not be null", ret);
		assertEquals("Filter must be called", paramValue + ServiceFilterChangeTreatment.VALUE, ret);
	}

	private final class ServiceFilterChangeTreatment extends Filter<IService> {
		public static final int VALUE = 500;

		private ServiceFilterChangeTreatment() {
			super("change treatment filter", 1);
		}

		@Override
		public Class<? extends IService> getService() {
			return IService.class;
		}

		@Override
		public IService getFilterServiceImpl(IService service) {
			return new IService() {
				@Override
				public int test4(DtoSample1 in1, Integer in2) {
					return 0;
				}

				@Override
				public DtoSample3 test3(DtoSample3 in) {
					return null;
				}

				@Override
				public DtoSample2 test2(DtoSample2 in) {
					return null;
				}

				@Override
				public DtoSample1 test1(DtoSample1 in) {
					return null;
				}

				@Override
				@FilteredMethod
				public int test0(int in) {
					return in + VALUE;
				}
			};
		}
	}

	@Test
	public void invoke_changeParam() throws Throwable {

		IService service = createMock(IService.class);

		// expect
		int paramValue = 10;
		int retValue = 20;
		expect(service.test0(paramValue + 1)).andReturn(retValue);

		// replay
		replay(service);

		// test
		FilterService fs = new FilterService(null) {
			@Override
			public Filter<?> getActiveFilter(java.lang.Class<?> serviceClass, String methodName) {
				return new ServiceFilterChangeParam();
			};
		};
		Object ret = fs.invoke(service, false, IService.class.getDeclaredMethod("test0", int.class),
				new Object[] { paramValue });

		// check
		verify(service);
		assertNotNull("Result must not be null", ret);
		assertEquals("Filter must be called", retValue, ret);
	}

	private final class ServiceFilterChangeParam extends Filter<IService> {

		private ServiceFilterChangeParam() {
			super("change param filter", 1);
		}

		@Override
		public Class<? extends IService> getService() {
			return IService.class;
		}

		@Override
		public IService getFilterServiceImpl(final IService service) {
			return new IService() {
				@Override
				public int test4(DtoSample1 in1, Integer in2) {
					return 0;
				}

				@Override
				public DtoSample3 test3(DtoSample3 in) {
					return null;
				}

				@Override
				public DtoSample2 test2(DtoSample2 in) {
					return null;
				}

				@Override
				public DtoSample1 test1(DtoSample1 in) {
					return null;
				}

				@Override
				@FilteredMethod
				public int test0(int in) {
					return service.test0(in + 1);
				}
			};
		}
	}

	@Test
	public void invoke_serviceSubClass_noExtendsSearchToInterface() throws Throwable {

		ServiceImpl service = createMock(ServiceImpl.class);

		// expect
		int paramValue = 10;
		int retValue = 20;
		expect(service.test0(paramValue)).andReturn(retValue);

		// replay
		replay(service);

		// test
		FilterServiceWithCounter fs = new FilterServiceWithCounter();
		Object ret = fs.invoke(service, false, IService.class.getDeclaredMethod("test0", int.class),
				new Object[] { paramValue });

		// check
		verify(service);
		assertNotNull("Result must not be null", ret);
		assertEquals("Service must be called", retValue, ret);
		assertEquals("FilterService.getActiveFilter must be called only one time", 1, fs.getCount());
	}

	@Test
	public void invoke_serviceSubClass_extendsSearchToInterface() throws Throwable {

		ServiceImpl service = new ServiceImpl();

		// expect
		int paramValue = 10;

		// replay

		// test
		FilterServiceWithCounter fs = new FilterServiceWithCounter();
		Object ret = fs.invoke(service, true, IService.class.getDeclaredMethod("test0", int.class),
				new Object[] { paramValue });

		// check
		assertNotNull("Result must not be null", ret);
		assertEquals("Filter must be called", paramValue + ServiceFilterChangeTreatment.VALUE, ret);
		assertEquals("FilterService.getActiveFilter must be called only one twice", 2, fs.getCount());
	}

	class FilterServiceWithCounter extends FilterService {
		int count = 0;

		public FilterServiceWithCounter() {
			super(null);
		}

		public int getCount() {
			return count;
		}

		@Override
		public Filter<?> getActiveFilter(java.lang.Class<?> serviceClass, String methodName) {
			count++;
			System.out.println(serviceClass);
			if (serviceClass == IService.class)
				return new ServiceFilterChangeTreatment();
			else
				return null;
		}
	}

	@Test
	public void setFilterActiveStatus_changeActiveFilter() throws IOException {

		// test
		FilterService fs = new FilterService("./src/test/resources/others_filters");
		fs.initFilters();

		// test
		Filter<?> filter1 = fs.getActiveFilter(ServiceImpl.class, "test1");
		// check
		assertNotNull("Filter must not be null", filter1);

		// test
		fs.setFilterActiveStatus(filter1, false);
		Filter<?> filter2 = fs.getActiveFilter(ServiceImpl.class, "test1");
		// check
		assertNotNull("Filter must not be null", filter2);
		assertTrue("New active filter must not be equals to an unactive filter", filter2 != filter1);

		// test
		fs.setFilterActiveStatus(filter1, true);
		Filter<?> filter3 = fs.getActiveFilter(ServiceImpl.class, "test1");
		// check
		assertNotNull("Filter must not be null", filter3);
		assertTrue("New active filter must not be equals to first filter", filter3 == filter1);
	}

	@Test
	public void setFilterActiveStatus_changePriority() throws IOException {

		// test
		FilterService fs = new FilterService("./src/test/resources/others_filters");
		fs.initFilters();

		// test
		Filter<?> filter1 = fs.getActiveFilter(ServiceImpl.class, "test1");
		// check
		assertNotNull("Filter must not be null", filter1);

		// test
		fs.setFilterPriority(filter1, 0);
		Filter<?> filter2 = fs.getActiveFilter(ServiceImpl.class, "test1");
		// check
		assertNotNull("Filter must not be null", filter2);
		assertTrue("New active filter must not be equals to an lower priority filter", filter2 != filter1);

		// test
		fs.setFilterPriority(filter1, 100);
		Filter<?> filter3 = fs.getActiveFilter(ServiceImpl.class, "test1");
		// check
		assertNotNull("Filter must not be null", filter3);
		assertTrue("New higter priority filter must not be equals to first filter", filter3 == filter1);
	}
}