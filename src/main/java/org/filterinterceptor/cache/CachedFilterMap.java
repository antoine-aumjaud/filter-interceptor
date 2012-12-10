package org.filterinterceptor.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Create a HashMap container to manage locks.
 * <p>
 * This is quicker for read access than use {@link Hashtable} or a
 * {@link Collections#synchronizedMap}
 */
public class CachedFilterMap {
	/**
	 * Object use to lock access to cache
	 */
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock wLock = rwLock.writeLock();
	private final Lock rLock = rwLock.readLock();

	/**
	 * The map there data are cached
	 */
	private final Map<String, CachedFilter> data = new HashMap<String, CachedFilter>();

	/**
	 * Removes all of the mappings from this map
	 * <p>
	 * Use the write lock
	 * 
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		wLock.lock();
		try {
			data.clear();
		} finally {
			wLock.unlock();
		}
	}

	/**
	 * Returns a Set view of the keys contained in this map
	 * <p>
	 * Use the read lock
	 * 
	 * @return a set view of the keys contained in this map
	 * @see java.util.Map#keySet()
	 */
	public Set<String> keySet() {
		rLock.lock();
		try {
			return data.keySet();
		} finally {
			rLock.unlock();
		}
	}

	/**
	 * Returns the value to which the specified key is mapped, or null if this
	 * map contains no mapping for the key.
	 * <p>
	 * Use the read lock
	 * 
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public CachedFilter get(Object key) {
		rLock.lock();
		try {
			return data.get(key);
		} finally {
			rLock.unlock();
		}
	}

	/**
	 * Associates the specified value with the specified key in this map
	 * <p>
	 * Use the write lock
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key.
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public CachedFilter put(String key, CachedFilter value) {
		wLock.lock();
		try {
			return data.put(key, value);
		} finally {
			wLock.unlock();
		}
	}
}
