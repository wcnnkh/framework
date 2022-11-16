package io.basc.framework.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Optional;
import io.basc.framework.util.Return;

/**
 * 通过classloader来获取上下文的BeanFactory
 * 
 * @author shuchaowen
 *
 */
public class FactoryLoader {
	private static volatile Map<ClassLoader, BeanFactory> FACTORY_MAP = new HashMap<>(4);
	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

	@Nullable
	public static BeanFactory bind(ClassLoader classLoader, BeanFactory beanFactory) {
		Lock lock = LOCK.writeLock();
		lock.lock();
		BeanFactory source;
		try {
			source = FACTORY_MAP.put(classLoader, beanFactory);
		} finally {
			lock.unlock();
		}

		if (source == null) {
			source = getBeanFactory(classLoader.getParent());
		}

		return getParentBeanFactory(source, beanFactory).orElse(null);
	}

	public static Return<BeanFactory> getParentBeanFactory(BeanFactory source, BeanFactory exclude) {
		BeanFactory factory = source;
		while (factory != null) {
			if (factory == exclude) {
				return Return.error("BeanFactory cannot be nested circularly", factory.getParentBeanFactory());
			}
			factory = factory.getParentBeanFactory();
		}
		return Return.success(source);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends BeanFactory> T get(ClassLoader classLoader, Class<T> type) {
		Assert.requiredArgument(type != null, "type");
		if (classLoader == null) {
			return null;
		}

		Lock lock = LOCK.readLock();
		ClassLoader use = classLoader;
		BeanFactory beanFactory = null;
		while (use != null && beanFactory == null) {
			lock.lock();
			try {
				beanFactory = FACTORY_MAP.get(use);
			} finally {
				lock.unlock();
			}

			if (beanFactory != null && type.isInstance(beanFactory)) {
				return (T) beanFactory;
			}

			if (beanFactory == null) {
				use = classLoader.getParent();
			}
		}
		return null;
	}

	public static BeanFactory getBeanFactory(ClassLoader classLoader) {
		return get(classLoader, BeanFactory.class);
	}

	@Nullable
	public static BeanFactory getBeanFactory() {
		return getBeanFactory(Thread.currentThread().getContextClassLoader());
	}

	public static <T> Optional<T> getInstance(Class<? extends T> type) {
		BeanFactory beanFactory = getBeanFactory(type.getClassLoader());
		if (beanFactory == null || !beanFactory.isInstance(type)) {
			return Optional.empty();
		}

		return Optional.of(beanFactory.getInstance(type));
	}

	public static Optional<Object> getInstance(String name, ClassLoader classLoader) {
		BeanFactory beanFactory = getBeanFactory(classLoader);
		if (beanFactory == null || !beanFactory.isInstance(name)) {
			return Optional.empty();
		}

		return Optional.of(beanFactory.getInstance(name));
	}
}
