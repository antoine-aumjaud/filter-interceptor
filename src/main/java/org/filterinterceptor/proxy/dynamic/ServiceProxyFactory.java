package org.filterinterceptor.proxy.dynamic;

import java.lang.reflect.Proxy;

import org.filterinterceptor.FilterService;
import org.filterinterceptor.spi.Filter;


/**
 * This class create a proxy of a service
 * <p>
 * Each methods called on the service will be intercept by
 * {@code FilterInterceptor} class, and a Filter is applied and replace the real
 * method
 * 
 * @see FilterInterceptor
 * @see Filter
 */
public class ServiceProxyFactory {
	private final FilterService filterService;

	/**
	 * Constructor with serviceFilter used to manage filters and the invocation
	 * 
	 * @param filterService
	 *            the Service filter to use
	 */
	public ServiceProxyFactory(FilterService filterService) {
		this.filterService = filterService;
	}

	/**
	 * Create the proxy of the service
	 * 
	 * @param service
	 *            the service used to make a proxy
	 * @param extendToInterfaces
	 *            indicate if the search of filters is done on the service class
	 *            only or if it is done with the interfaces of the services too
	 * 
	 * @return the proxy
	 * 
	 * @throws IllegalStateException
	 *             if filter service is not defined
	 * @throws NullPointerException
	 *             if service is null
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T createProxy(T service, boolean extendToInterfaces) {
		if (filterService == null)
			throw new IllegalStateException("You must set an filter service");
		if (service == null)
			throw new NullPointerException("You must set the service");

		Class<?> serviceClass = service.getClass();
		return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), serviceClass.getInterfaces(),
				new FilterInterceptor(filterService, service, extendToInterfaces));
	}
}
