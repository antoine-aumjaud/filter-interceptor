package org.filterinterceptor.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.filterinterceptor.FilterService;

/**
 * This class is used by java dynamic proxy to redirect all service methods call
 * on ServiceFilter invoke method
 * 
 * @param <T>
 *            the type of the Service
 * 
 * @see ServiceProxyFactory
 * @see FilterService
 */
public class FilterInterceptor<T> implements InvocationHandler {

	private final FilterService filterService;
	private final T service;
	private final boolean extendToInterfaces;

	/**
	 * Created by Service Proxy Factory
	 * 
	 * @param filterService
	 *            the filter service container
	 * @param service
	 *            the target service for proxy creation
	 * @param extendToInterfaces
	 *            a boolean indicate if the search of filter is done on filter
	 *            interfaces too or not
	 */
	/* package */FilterInterceptor(FilterService filterService, T service, boolean extendToInterfaces) {
		this.filterService = filterService;
		this.service = service;
		this.extendToInterfaces = extendToInterfaces;
	}

	/**
	 * This method is call automatically by the proxy and delegate treatment to
	 * FilterService
	 */
	@Override
	public Object invoke(Object o, Method method, Object[] args) throws Throwable {
		return filterService.invoke(service, extendToInterfaces, method, args);
	}
}
