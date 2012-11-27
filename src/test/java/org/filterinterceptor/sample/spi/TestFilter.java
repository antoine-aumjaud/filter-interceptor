package org.filterinterceptor.sample.spi;

import org.filterinterceptor.sample.service.DtoSample1;
import org.filterinterceptor.sample.service.DtoSample2;
import org.filterinterceptor.sample.service.DtoSample3;
import org.filterinterceptor.sample.service.IService;
import org.filterinterceptor.sample.service.ServiceImpl;
import org.filterinterceptor.spi.Filter;
import org.filterinterceptor.spi.FilteredMethod;

public class TestFilter extends Filter<IService> {

	public TestFilter() {
		super("Service ServiceImpl Test", 1);
	}

	@Override
	public Class<ServiceImpl> getService() {
		return ServiceImpl.class;
	}

	@Override
	public IService getFilterServiceImpl(IService service) {
		return new ServiveFilter(service);
	}

	private final static class ServiveFilter implements IService {
		private final IService service;

		public ServiveFilter(IService service) {
			this.service = service;
		}

		@FilteredMethod
		@Override
		public int test(int in) {
			return service.test(in);
		}

		@Override
		public int test0(int in) {
			return 0;
		}

		@Override
		public DtoSample1 test1(DtoSample1 in) {
			return null;
		}

		@Override
		public DtoSample2 test2(DtoSample2 in) {
			return null;
		}

		@Override
		public DtoSample3 test3(DtoSample3 in) {
			return null;
		}

		@Override
		public int test4(DtoSample1 in1, Integer in2) {
			return 0;
		}

	}
}
