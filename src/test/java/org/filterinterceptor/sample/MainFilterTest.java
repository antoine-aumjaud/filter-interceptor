package org.filterinterceptor.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.management.FilterServiceMBeansRegister;
import org.filterinterceptor.proxy.dynamic.ServiceProxyFactory;
import org.filterinterceptor.sample.service.DtoSample1;
import org.filterinterceptor.sample.service.DtoSample2;
import org.filterinterceptor.sample.service.DtoSample3;
import org.filterinterceptor.sample.service.IService;
import org.filterinterceptor.sample.service.ServiceImpl;
import org.filterinterceptor.spi.Filter;

public final class MainFilterTest {
	public static void main(String[] args) {

		// Create Filter bean service
		FilterService filterService = new FilterService("./src/test/resources/others_filters");
		// Init filters
		try {
			filterService.initFilters();
		} catch (IOException e) {
			throw new RuntimeException("Can't init filters: " + e.getMessage(), e);
		}

		// Init JMX
		FilterServiceMBeansRegister register = new FilterServiceMBeansRegister(filterService);
		register.initMBean();

		// Create real bean service
		IService s = new ServiceImpl();

		// Create proxy service
		ServiceProxyFactory serviceProxyFactory = new ServiceProxyFactory(filterService);
		IService sProxy = serviceProxyFactory.createProxy(s, false);

		// Actions!
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader b = new BufferedReader(is);
		char command = '\0';
		System.out.println("App running");
		while (command != 'q') {
			List<Filter<?>> filters = filterService.getAllFilters();
			Map<String, Filter<?>> filtersMap = filterService.getAllActiveFiltersUsed();
			System.out
					.printf("====Menu===%nr: reinit%nl: filter list%na: activate%nd: desactivate%np: change prority%nu: used filters%nt: test%nq: quit%n>");
			try {
				String commandLine = b.readLine();
				if (commandLine.length() > 0) {
					command = commandLine.charAt(0);
					switch (command) {
					case 'q':
						break;
					case 'r':
						filterService.initFilters();
						break;
					case 'a':
					case 'd': {
						Filter<?> filter = getFilter(filters, commandLine);
						if (filter != null) {
							filterService.setFilterActiveStatus(filter, command != 'd');
							printFilterList(filters);
						}
						break;
					}
					case 'p': {
						Filter<?> filter = getFilter(filters, commandLine);
						if (filter != null) {
							System.out.printf("Current priority is %d, enter new Priority: ", filter.getPriority());
							commandLine = b.readLine();
							try {
								int priority = Integer.valueOf(commandLine);
								filterService.setFilterPriority(filter, priority);
								printFilterList(filters);
							} catch (NumberFormatException e) {
								System.err.printf("Wrong priority: %s%n", commandLine);
							}
						}
						break;
					}
					case 'l':
						printFilterList(filters);
						break;
					case 'u':
						for (Map.Entry<String, Filter<?>> mapEntry : filtersMap.entrySet()) {
							System.out.printf("%s -> %s%n", mapEntry.getKey(), mapEntry.getValue().getDescription());
						}
						break;
					case 't':
						System.out.println("Start Test with Proxy");
						long startTestProxy = System.nanoTime();
						launchTest(sProxy);
						long stopTestProxy = System.nanoTime();
						System.out.println();
						System.out.println("Start Test without Proxy");
						long startTest = System.nanoTime();
						launchTest(s);
						long stopTest = System.nanoTime();
						double testProxy = stopTestProxy - startTestProxy;
						double testDirect = stopTest - startTest;
						System.out.printf("Exceution of PROXY test: %fms%n", testProxy / 1000000);
						System.out.printf("Exceution of REAL  test: %fms%n", testDirect / 1000000);
						System.out.printf("Overtime: %fms%n", (testProxy - testDirect) / 1000000);
						break;
					default:
						System.err.println("Please, enter a valid command");
						break;
					}
				} else {
					System.err.println("Please, enter a command");
				}
			} catch (IOException e) {
				System.err.printf("Can't read in: %s%n", e.getMessage());
			} finally {
				System.out.println("###Action completed###");
			}
		}

		// Destroy JMX
		register.resetMBean();

		// End
		System.out.println("App closed");
	}

	/**
	 * Get the filter in a list of filter with an index in a command line
	 * 
	 * @param filters
	 *            the list of filter
	 * @param commandLine
	 *            the index in a string
	 * @return the filter found, null otherwise
	 */
	private static Filter<?> getFilter(List<Filter<?>> filters, String commandLine) {
		int pos = -1;
		try {
			pos = Integer.valueOf(commandLine.substring(1));
			Filter<?> filter = filters.get(pos - 1);
			return filter;
		} catch (NumberFormatException e) {
			System.err
					.printf("Command must be followed immediately (without any space) by the index of the filter to change priority%n");
		} catch (IndexOutOfBoundsException e) {
			System.err.printf("Can't find filter at %d%n", pos);
		}
		return null;
	}

	/**
	 * Print the list of filters in standard output
	 * 
	 * @param filters
	 *            the list of filters to print
	 */
	private static void printFilterList(List<Filter<?>> filters) {
		int index = 1;
		for (Filter<?> filter : filters) {
			System.out.printf("%d: %s%n", index++, filter);
		}
	}

	/**
	 * Launch tests
	 */
	private static void launchTest(IService s) {
		System.out.println("S1");
		DtoSample1 s1 = new DtoSample1(1, 2.2, "S1", Arrays.asList("AA", "BB", "CC"));
		System.out.printf("before: %s%n", s1);
		s1 = s.test1(s1);
		System.out.printf("after: %s%n", s1);

		System.out.println();
		System.out.println("S2");
		DtoSample2 s2 = new DtoSample2(1, 2.3, "S2", s1);
		System.out.printf("before: %s%n", s2);
		s2 = s.test2(s2);
		System.out.printf("after: %s%n", s2);

		System.out.println();
		System.out.println("S3");
		DtoSample3 s3 = new DtoSample3(BigDecimal.TEN);
		System.out.printf("before: %s%n", s3);
		s3 = s.test3(s3);
		System.out.printf("after: %s%n", s3);

		System.out.println();
		System.out.println("S4");
		int in = 2;
		System.out.printf("before: %d%n", s1.getA() + in);
		int res = s.test4(s1, in);
		System.out.printf("after: %d%n", res);
	}
}
