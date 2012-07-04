package org.filterinterceptor.sample.service;

public interface IService {

	int test0(int in);

	public DtoSample1 test1(DtoSample1 in);

	public DtoSample2 test2(DtoSample2 in);

	public DtoSample3 test3(DtoSample3 in);

	public int test4(DtoSample1 in1, Integer in2);

}