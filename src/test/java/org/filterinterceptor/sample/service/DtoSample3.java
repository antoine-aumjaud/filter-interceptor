package org.filterinterceptor.sample.service;

import java.math.BigDecimal;

public class DtoSample3 {
	private BigDecimal b;

	public DtoSample3(BigDecimal b) {
		super();
		this.b = b;
	}

	public String toString() {
		return "b= " + b;
	}

	public BigDecimal getB() {
		return b;
	}

	public void setB(BigDecimal b) {
		this.b = b;
	}

}
