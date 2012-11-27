package org.filterinterceptor.sample.service;

public interface IService {

	int test(int in);

	int test0(int in);

	DtoSample1 test1(DtoSample1 in);

	DtoSample2 test2(DtoSample2 in);

	DtoSample3 test3(DtoSample3 in);

	int test4(DtoSample1 in1, Integer in2);

}