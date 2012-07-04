package org.filterinterceptor.sample.service;

import java.util.List;

public class DtoSample1 {
	private int a;
	private double b;
	private String c;
	private List<String> list;

	public DtoSample1(int a, double b, String c, List<String> list) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.list = list;
	}

	public String toString() {
		return "a= " + a + ", b= " + b + ", c=" + c + ", list=" + list.toString();
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
}
