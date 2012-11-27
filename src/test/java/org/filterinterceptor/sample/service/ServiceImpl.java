package org.filterinterceptor.sample.service;

public class ServiceImpl implements IService {

	@Override
	public int test(int in) {
		return in;
	}

	@Override
	public int test0(int in) {
		return in;
	}

	@Override
	public DtoSample1 test1(DtoSample1 in) {
		return in;
	}

	@Override
	public DtoSample2 test2(DtoSample2 in) {
		return in;
	}

	@Override
	public DtoSample3 test3(DtoSample3 in) {
		return in;
	}

	@Override
	public int test4(DtoSample1 in1, Integer in2) {
		return in1.getA() * in2;
	}

}
