package org.filterinterceptor.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Create a child of HashMap to manage lock
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

	/*
	 * (non-Javadoc)
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

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
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

	/*
	 * (non-Javadoc)
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
