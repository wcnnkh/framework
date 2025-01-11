package io.basc.framework.beans.factory.support;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.SingletonBeanRegistry;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.alias.SimpleAliasRegistry;
import io.basc.framework.util.collections.Elements;

public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private volatile Map<String, Object> singletonObjects = new LinkedHashMap<>();

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	@Override
	public void registerSingleton(String name, Object singletonObject) throws BeansException {
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			if (containsSingleton(name)) {
				throw new BeansException("Single instance '" + name + "' already already exists or alias '"
						+ getAliases(name) + "' already exists");
			}
			singletonObjects.put(name, singletonObject);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void removeSingleton(String name) throws BeansException {
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			if (singletonObjects.remove(name) != null) {
				// 存在
				return;
			}

			// 不存在，尝试别名
			for (String alias : getAliases(name)) {
				if (singletonObjects.remove(alias) != null) {
					// 存在
					return;
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Object getSingleton(String name) throws BeansException {
		Lock readLock = readWriteLock.readLock();
		readLock.lock();
		try {
			Object singleton = singletonObjects.get(name);
			if (singleton == null) {
				for (String alias : getAliases(name)) {
					singleton = singletonObjects.get(alias);
					if (singleton != null) {
						break;
					}
				}
			}
			return singleton;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean containsSingleton(String name) {
		Lock readLock = readWriteLock.readLock();
		readLock.lock();
		try {
			if (singletonObjects.containsKey(name)) {
				return true;
			}

			for (String alias : getAliases(name)) {
				if (singletonObjects.containsKey(alias)) {
					return true;
				}
			}
			return false;
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Elements<String> getRegistrationOrderSingletonNames() {
		Lock readLock = readWriteLock.readLock();
		readLock.lock();
		try {
			String[] array = StringUtils.toStringArray(singletonObjects.keySet());
			return Elements.forArray(array);
		} finally {
			readLock.unlock();
		}
	}
}
