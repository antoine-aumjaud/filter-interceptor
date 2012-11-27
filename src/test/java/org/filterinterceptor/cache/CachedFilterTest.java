package org.filterinterceptor.cache;

import org.junit.Test;

import static org.junit.Assert.*;

public class CachedFilterTest {

	@Test
	public void checkGetters() {
		String filterDescription = "EMPTY";
		Object filteredService = new Object();

		CachedFilter cf = new CachedFilter(filterDescription, filteredService);

		assertEquals(filterDescription, cf.getFilterDescription());
		assertEquals(filteredService, cf.getFilteredService());
	}
}
