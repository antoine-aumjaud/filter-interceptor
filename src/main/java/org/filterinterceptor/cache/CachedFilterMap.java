package org.filterinterceptor.cache;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Create a child of HashMap to manage lock
 */
@SuppressWarnings("serial")
public class CachedFilterMap extends HashMap<String, CachedFilter> {
	/**
	 * Object use to lock access to cache
	 */
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#clear()
	 */
	@Override
	public void clear() {
		Lock lock = rwLock.writeLock();
		try {
			lock.lock();
			super.clear();
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#keySet()
	 */
	@Override
	public Set<String> keySet() {
		Lock lock = rwLock.readLock();
		try {
			lock.lock();
			return super.keySet();
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public CachedFilter get(Object key) {
		Lock lock = rwLock.readLock();
		try {
			lock.lock();
			return super.get(key);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public CachedFilter put(String key, CachedFilter value) {
		Lock lock = rwLock.writeLock();
		try {
			lock.lock();
			return super.put(key, value);
		} finally {
			lock.unlock();
		}
	}
}
