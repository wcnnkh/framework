package io.basc.framework.context.support;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.context.ApplicationContext;
import io.basc.framework.util.Assert;
import io.basc.framework.util.comparator.ClassLoaderComparator;

/**
 * @see GenericApplicationContext#start()
 * @author wcnnkh
 *
 */
public final class ContextLoader {
	private ContextLoader() {
	}

	private static TreeMap<ClassLoader, ApplicationContext> applicationMap = new TreeMap<>(
			ClassLoaderComparator.global());
	private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public static ApplicationContext getApplicationContext(ClassLoader classLoader) {
		Assert.requiredArgument(classLoader != null, "classLoader");
		Lock readLock = readWriteLock.readLock();
		readLock.lock();
		try {
			Entry<ClassLoader, ApplicationContext> entry = applicationMap.ceilingEntry(classLoader);
			return entry == null ? null : entry.getValue();
		} finally {
			readLock.unlock();
		}
	}

	public static ApplicationContext getCurrentApplicationContext() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			return getDefaultApplicationContext();
		}
		ApplicationContext applicationContext = getApplicationContext(classLoader);
		return applicationContext == null ? getDefaultApplicationContext() : applicationContext;
	}

	public static ApplicationContext getDefaultApplicationContext() {
		Lock readLock = readWriteLock.readLock();
		readLock.lock();
		try {
			if (applicationMap.size() == 1) {
				return applicationMap.firstEntry().getValue();
			}
			return null;
		} finally {
			readLock.unlock();
		}
	}

	public static boolean removeApplicationContext(ApplicationContext applicationContext) {
		ClassLoader classLoader = applicationContext.getClassLoader();
		if (classLoader == null) {
			return false;
		}
		return removeApplicationContext(classLoader, applicationContext);
	}

	public static ApplicationContext removeApplicationContext(ClassLoader classLoader) {
		Assert.requiredArgument(classLoader != null, "classLoader");
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			ApplicationContext applicationContext = applicationMap.remove(classLoader);
			removeApplicationContextAfter(applicationContext);
			return applicationContext;
		} finally {
			writeLock.unlock();
		}
	}

	public static boolean removeApplicationContext(ClassLoader classLoader, ApplicationContext applicationContext) {
		Assert.requiredArgument(classLoader != null, "classLoader");
		Assert.requiredArgument(applicationContext != null, "applicationContext");
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			if (applicationMap.remove(classLoader, applicationContext)) {
				removeApplicationContextAfter(applicationContext);
				return true;
			}
			return false;
		} finally {
			writeLock.unlock();
		}
	}

	private static void removeApplicationContextAfter(ApplicationContext applicationContext) {
		ApplicationContext parent = applicationContext.getParent();
		if (parent != null && parent.getClassLoader() != null) {
			if (getApplicationContext(parent.getClassLoader()) == null) {
				setApplicationContext(parent);
			}
		}
	}

	public static ApplicationContext setApplicationContext(ApplicationContext applicationContext) {
		Assert.requiredArgument(applicationContext != null, "applicationContext");
		return setApplicationContext(applicationContext.getClassLoader(), applicationContext);
	}

	public static ApplicationContext setApplicationContext(ClassLoader classLoader,
			ApplicationContext applicationContext) {
		Assert.requiredArgument(classLoader != null, "classLoader");
		Assert.requiredArgument(applicationContext != null, "applicationContext");
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			return applicationMap.put(classLoader, applicationContext);
		} finally {
			writeLock.unlock();
		}
	}
}
