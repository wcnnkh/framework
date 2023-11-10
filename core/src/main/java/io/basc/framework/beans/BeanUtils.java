package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public class BeanUtils {
	private static final ServiceLoader<BeaninfoFactory> BEANINFO_FACTORIES = ServiceLoader.load(BeaninfoFactory.class);
	private static volatile Map<Class<?>, BeanMapping> beanMappingCacheMap = new HashMap<>();

	/**
	 * Introspect on a Java Bean and learn about all its properties, exposed
	 * methods, and events.
	 * <p>
	 * If the BeanInfo class for a Java Bean has been previously Introspected then
	 * the BeanInfo class is retrieved from the BeanInfo cache.
	 *
	 * @param beanClass The bean class to be analyzed.
	 * @return A BeanInfo object describing the target bean.
	 * @exception IntrospectionException if an exception occurs during
	 *                                   introspection.
	 * @see #flushCaches
	 * @see #flushFromCaches
	 */
	public static BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
		BeanInfo beanInfo = null;
		for (BeaninfoFactory factory : BEANINFO_FACTORIES) {
			beanInfo = factory.getBeaninfo(beanClass);
			if (beanInfo != null) {
				return beanInfo;
			}
		}
		return Introspector.getBeanInfo(beanClass);
	}

	public BeanMapping getBeanMapping(Class<?> beanClass) throws BeansException {
		BeanMapping beanMapping = beanMappingCacheMap.get(beanClass);
		if (beanMapping == null) {
			synchronized (beanMappingCacheMap) {
				beanMapping = beanMappingCacheMap.get(beanClass);
				if (beanMapping == null) {
					try {
						BeanInfo beanInfo = getBeanInfo(beanClass);
						beanMapping = new BeanMapping(beanClass, beanInfo);
						beanMappingCacheMap.put(beanClass, beanMapping);
					} catch (IntrospectionException e) {
						throw new FatalBeanException(
								"Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", e);
					}
				}
			}
		}
		return beanMapping;
	}

	/**
	 * Check whether the given class is cache-safe in the given context, i.e.
	 * whether it is loaded by the given ClassLoader or a parent of it.
	 * 
	 * @param clazz       the class to analyze
	 * @param classLoader the ClassLoader to potentially cache metadata in (may be
	 *                    {@code null} which indicates the system class loader)
	 */
	public static boolean isCacheSafe(Class<?> clazz, @Nullable ClassLoader classLoader) {
		Assert.notNull(clazz, "Class must not be null");
		try {
			ClassLoader target = clazz.getClassLoader();
			// Common cases
			if (target == classLoader || target == null) {
				return true;
			}
			if (classLoader == null) {
				return false;
			}
			// Check for match in ancestors -> positive
			ClassLoader current = classLoader;
			while (current != null) {
				current = current.getParent();
				if (current == target) {
					return true;
				}
			}
			// Check for match in children -> negative
			while (target != null) {
				target = target.getParent();
				if (target == classLoader) {
					return false;
				}
			}
		} catch (SecurityException ex) {
			// Fall through to loadable check below
		}

		// Fallback for ClassLoaders without parent/child relationship:
		// safe if same Class can be loaded from given ClassLoader
		return (classLoader != null && isLoadable(clazz, classLoader));
	}

	/**
	 * Check whether the given class is loadable in the given ClassLoader.
	 * 
	 * @param clazz       the class to check (typically an interface)
	 * @param classLoader the ClassLoader to check against
	 */
	private static boolean isLoadable(Class<?> clazz, ClassLoader classLoader) {
		try {
			return (clazz == classLoader.loadClass(clazz.getName()));
			// Else: different class with same name found
		} catch (ClassNotFoundException ex) {
			// No corresponding class found at all
			return false;
		}
	}
}
