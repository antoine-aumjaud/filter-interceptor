package org.filterinterceptor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.filterinterceptor.cache.CachedFilter;
import org.filterinterceptor.cache.CachedFilterMap;
import org.filterinterceptor.spi.Filter;
import org.filterinterceptor.spi.FilteredMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the entry point of this API
 * <p>
 * The aim of this class is to:
 * <ul>
 * <li>Load JAR of a directory
 * <li>Load all filters (user SPI)
 * <li>Provide simple methods to access to these filters
 * <li>Launch method invocation
 * </ul>
 * You must call initFilter before first use.
 */
public class FilterService extends Observable {

	private static final Logger logger = LoggerFactory.getLogger(FilterService.class);

	/**
	 * The place of JAR to load
	 */
	private final String jarFolder;

	/**
	 * Set of filters
	 */
	private final Set<Filter<?>> loadedFilters = new HashSet<Filter<?>>();

	/**
	 * Active filters in a map to access to used Filter quickly
	 */
	private Map<String, Filter<?>> activeFilters;
	/**
	 * Filters in a list to manage activation
	 */
	private List<Filter<?>> allFilters;

	/**
	 * Object use to lock access to collections for fast access
	 */
	private final ReadWriteLock lockToFilterFastAccessCollections = new ReentrantReadWriteLock();

	/*
	 * CACHE
	 */
	/**
	 * Indicate if cache of filtered services is active
	 */
	private boolean isCacheActive;

	/**
	 * Cache of filtered services
	 */
	private final Map<String, CachedFilter> cacheFilteredServices = new CachedFilterMap();

	/*
	 * CONSTRUCTORS
	 */
	/**
	 * Public constructor
	 * 
	 * @param jarFolder
	 *            the folder of JAR containing Filters to load
	 */
	public FilterService(String jarFolder) {
		this.jarFolder = jarFolder;
	}

	/**
	 * Public constructor
	 * 
	 * @param jarFolder
	 *            the folder of JAR containing Filters to load
	 * @param isCacheActive
	 *            a boolean indicate if a cache is used for filtered service
	 */
	public FilterService(String jarFolder, boolean isCacheActive) {
		this.jarFolder = jarFolder;
		this.isCacheActive = isCacheActive;
	}

	/*
	 * PUBLIC
	 */
	/**
	 * Indicate if cache of filtered service is active
	 * 
	 * @return true if cache is active
	 */
	public boolean isCacheActive() {
		return isCacheActive;
	}

	/**
	 * Activate or desactivate the filtered service cache
	 * 
	 * @param isCacheActive
	 *            a boolean to set the activation status
	 */
	public void setCacheActive(boolean isCacheActive) {
		this.isCacheActive = isCacheActive;
		if (!isCacheActive)
			clearCache();
	}

	/**
	 * Clear the filtered service cache
	 */

	public void clearCache() {
		cacheFilteredServices.clear();
	}

	/**
	 * Get cache keys
	 * 
	 * @return a set of cache keys
	 */
	public Set<String> getCacheKeys() {
		return cacheFilteredServices.keySet();
	}

	/**
	 * Get the active filter for one param of one method on one service
	 * 
	 * @param serviceClass
	 *            the service class
	 * @param methodName
	 *            the method name
	 * @return the active filter with the highest priority
	 */
	public Filter<?> getActiveFilter(Class<?> serviceClass, String methodName) {
		Lock lock = lockToFilterFastAccessCollections.readLock();
		try {
			lock.lock();
			String key = getKey(serviceClass, methodName);
			logger.trace("Access to key {}", key);
			return activeFilters.get(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Get all active filters which be used
	 * 
	 * @return the Map of filters
	 */
	public Map<String, Filter<?>> getAllActiveFiltersUsed() {
		Lock lock = lockToFilterFastAccessCollections.readLock();
		try {
			lock.lock();
			return activeFilters;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Get all filters sorted in an unmodifiable list
	 * 
	 * @return the list of filters
	 */
	public List<Filter<?>> getAllFilters() {
		Lock lock = lockToFilterFastAccessCollections.readLock();
		try {
			lock.lock();
			return allFilters;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Load dynamically Filters in JAR files
	 * 
	 * @throws IOException
	 *             on exception during research
	 */
	public void initFilters() throws IOException {
		logger.debug("Search Filters...");

		try {
			// Search not loaded JAR files and build class loader
			ClassLoader classLoader = loadExternalJar();

			// Discover and register the available commands
			buildLoadedFilter(classLoader);

		} catch (RuntimeException e) {
			logger.error("Can't init filter: " + e.getMessage(), e);
		}

		// Clear cache
		clearCache();

		// Build fast access collections
		buildFilterFastAccessCollections();

		// Notify observer (like MBeanRegister)
		notifyObservers();

		logger.debug("Search Filters - End");
	}

	/**
	 * Active or desactive a Filter
	 * 
	 * @param filter
	 *            the filter to activate or desactivate
	 * @param active
	 *            the active status to set
	 */
	public void setFilterActiveStatus(Filter<?> filter, boolean active) {
		// Set active state on filter
		filter.setActive(active);

		// Clear cache
		clearCache();

		// Build fast access collections
		buildFilterFastAccessCollections();
	}

	/**
	 * Change priority of a Filter
	 * 
	 * @param filter
	 *            the filter to change the priority
	 * @param priority
	 *            the priority to set
	 */
	public void setFilterPriority(Filter<?> filter, int priority) {
		// Set priority on filter
		filter.setPriority(priority);

		// Clear cache
		clearCache();

		// Build fast access collections
		buildFilterFastAccessCollections();
	}

	/**
	 * THis method search and call the filter of a service, and the real service
	 * if no filter is found
	 * 
	 * @param service
	 *            the real service
	 * @param extendToInterfaces
	 *            a boolean indicate if the search of filter is done on filter
	 *            interfaces too or not
	 * @param method
	 *            the method to call
	 * @param args
	 *            parameters to give to the method called
	 * @return the returned object by Filter or the real service
	 * @throws Throwable
	 *             if invoked method throw one
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object invoke(Object service, boolean extendToInterfaces, Method method, Object... args) throws Throwable {
		if (service == null)
			throw new IllegalArgumentException("Service can't be null");

		Object proxy = null;
		String filterDescription = null;

		// Get proxy
		try {
			String methodName = method.getName();
			String cacheKey =
			// service.getClass().getName() + "." + methodName;
			service.getClass().getName() + System.identityHashCode(service) + "." + methodName;

			// Search in cache
			if (isCacheActive) {
				logger.debug("Search filter on {} in cache", cacheKey);
				CachedFilter cache = cacheFilteredServices.get(cacheKey);
				if (cache != null) {
					logger.trace("Filter found in cache");
					proxy = cache.getFilteredService();
					filterDescription = "from cache: " + cache.getFilterDescription();
				} else {
					logger.trace("Filter NOT found in cache");
				}
			}

			// Search a existing one
			if (proxy == null) {
				Class<?> serviceClass = service.getClass();
				logger.debug("Search filter on {}.{}", serviceClass.getSimpleName(), methodName);

				// Get the filters
				Filter filter = getActiveFilter(serviceClass, methodName);
				// if no filter on service class and extended search to
				// interface is activated
				if (filter == null && extendToInterfaces) {
					for (Class<?> i : serviceClass.getInterfaces()) {
						filter = getActiveFilter(i, methodName);
						if (filter != null)
							break;
					}
				}

				if (filter != null) {
					// Get the new method proxy
					proxy = filter.getFilterServiceImpl(service);
					filterDescription = "filter: " + filter.getDescription();

					// Put proxy in cache if necessary
					if (isCacheActive) {
						logger.debug("Add filter in cache");
						cacheFilteredServices.put(cacheKey, new CachedFilter(filter.getDescription(), proxy));
					}
				} else {
					logger.trace("There is no filter on this service");
					proxy = service;
					filterDescription = "real service method";

					// Put service in cache if necessary
					if (isCacheActive) {
						logger.debug("Add service in cache");
						cacheFilteredServices.put(cacheKey, new CachedFilter(filterDescription, service));
					}
				}
			}
		} catch (RuntimeException e) {
			logger.error("Exception while try to execute filter: " + e.getMessage(), e);
			proxy = service;
			filterDescription = "real service method (SEE ERROR IN LOG)";
		}

		// Call the final method
		try {
			// Call the method proxy
			logger.info("Invoke {}", filterDescription);
			return method.invoke(proxy, args);
		} catch (Throwable t) {
			if (t.getCause() != null)
				// Method invoke add a Exception level in the stack
				throw t.getCause();
			else
				throw t;
		}
	}

	/*
	 * PRIVATE
	 */

	/**
	 * Set all JAR files in a Class Loader
	 * 
	 * @return a classLoader containing all JAR files URL
	 * @throws IOException
	 *             if JAR folder not found
	 */
	private ClassLoader loadExternalJar() throws IOException {

		File baseDirectory = new File(jarFolder);
		logger.debug("Try to find JAR in folder: {}", baseDirectory.getCanonicalPath());
		// Check if directory exists
		if (!baseDirectory.exists() || !baseDirectory.isDirectory())
			throw new IOException("Extern JAR folder (" + baseDirectory.getCanonicalPath() + ") not found");
		// Get file with .jar extension
		File[] jarFiles = baseDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		// Add jarFiles to classLoader
		ClassLoader classLoader;
		if (jarFiles != null) {
			List<URL> urlList = new ArrayList<URL>();
			for (File file : jarFiles) {
				logger.debug("JAR found: {}", file.getName());
				URL url = file.toURI().toURL();
				urlList.add(url);
			}
			classLoader = new URLClassLoader(urlList.toArray(new URL[0]), this.getClass().getClassLoader());
		} else {
			classLoader = this.getClass().getClassLoader();
		}
		return classLoader;
	}

	/**
	 * Load all classes in classLoader with the Service Provider API
	 * 
	 * @param classLoader
	 *            the class loader use by the Service Provider API
	 */
	private void buildLoadedFilter(ClassLoader classLoader) {

		logger.debug("Load filters in classpath...");
		@SuppressWarnings("rawtypes")
		ServiceLoader<Filter> filterLoader = ServiceLoader.load(Filter.class, classLoader);

		logger.debug("Register filters...");
		for (Filter<?> newFilter : filterLoader) {
			// Check that filter has not been already loaded
			if (!loadedFilters.contains(newFilter)) {
				loadedFilters.add(newFilter);
				logger.debug("Filter found on service {}: <{}>", newFilter.getService().getSimpleName(),
						newFilter.getDescription());
				// Set the a modification on Observable
				setChanged();
			}
		}
	}

	/**
	 * Build the map key with service Parameter
	 * 
	 * @param serviceClass
	 *            the class of the service
	 * @param methodName
	 *            the method name
	 * @return the key of the map used to store filter
	 */
	private static String getKey(Class<?> serviceClass, String methodName) {
		String key = serviceClass.getName() + "." + methodName;
		return key;
	}

	/**
	 * Build a the access collection to improve performances
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	// TODOJ7: to remove
	private void buildFilterFastAccessCollections() {

		Lock lock = lockToFilterFastAccessCollections.writeLock();
		try {
			lock.lock();

			// LIST
			allFilters = new ArrayList<Filter<?>>(loadedFilters);
			// Sort Filters
			Collections.sort((List) allFilters); // TODOJ7: cast for JDK 1.6 fix
													// complication error
			// Create a RO list
			allFilters = Collections.unmodifiableList(allFilters);

			// MAP
			activeFilters = new HashMap<String, Filter<?>>();
			for (Filter<?> filter : loadedFilters) {
				if (filter.isActive()) {
					// Iterate on each method
					// and check that the annotation FilteredMethod is present
					logger.debug("Read filter methods on {}: <{}>", filter.getService().getSimpleName(),
							filter.getDescription());
					for (Method method : filter.getFilterServiceImpl(null).getClass().getDeclaredMethods()) {
						if (method.getAnnotation(FilteredMethod.class) != null) {
							String methodName = method.getName();
							String filterKey = getKey(filter.getService(), methodName);
							logger.debug("Filter <{}>: {}.{} is overrided", new String[] { filter.getDescription(),
									filter.getService().getSimpleName(), methodName });

							Filter<?> otherFilter = activeFilters.get(filterKey);
							if (otherFilter == null || otherFilter.getPriority() < filter.getPriority()) {
								logger.trace("New Filter set for key {}", filterKey);
								activeFilters.put(filterKey, filter);
							}
						}
					}
				} else {
					logger.debug("Filter <{}> is desactivated", filter.getDescription());
				}

			}
			// Create a RO map
			activeFilters = Collections.unmodifiableMap(activeFilters);
		} finally {
			lock.unlock();
		}
	}
}