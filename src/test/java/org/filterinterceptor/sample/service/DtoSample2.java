package org.filterinterceptor.sample.service;

public class DtoSample2 {
	private Integer a;
	private Double b;
	private String c;
	private DtoSample1 dto;

	public DtoSample2(Integer a, Double b, String c, DtoSample1 dto) {
		super();
		this.a = a;
		this.b = b;
		this.c = c;
		this.dto = dto;
	}

	public String toString() {
		return "a= " + a + ", b= " + b + ", c=" + c + ", dto=" + dto.toString();
	}

	public Integer getA() {
		return a;
	}

	public void setA(Integer a) {
		this.a = a;
	}

	public Double getB() {
		return b;
	}

	public void setB(Double b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public DtoSample1 getDto() {
		return dto;
	}

	public void setDto(DtoSample1 dto) {
		this.dto = dto;
	}

}
