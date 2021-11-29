package io.basc.framework.context.support;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;

public abstract class AbstractClassesLoader implements ClassesLoader, ClassLoaderProvider {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private volatile Set<Class<?>> classes;
	private ClassLoaderProvider classLoaderProvider;
	private Predicate<Class<?>> predicate;

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public Predicate<Class<?>> getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate<Class<?>> predicate) {
		this.predicate = predicate;
	}

	public void reload() {
		synchronized (this) {
			this.classes = getClasses(getClassLoader());
		}
	}

	public Iterator<Class<?>> iterator() {
		if (classes == null) {
			synchronized (this) {
				if (classes == null) {
					classes = getClasses(getClassLoader());
				}
			}
		}
		return classes.iterator();
	}

	protected abstract Stream<Class<?>> load(ClassLoader classLoader);

	/**
	 * no cache
	 * 
	 * @param classLoader
	 * @return
	 */
	public Set<Class<?>> getClasses(ClassLoader classLoader) {
		return load(classLoader).filter((c) -> {
			return ClassUtils.isAvailable(c) && ReflectionUtils.isAvailable(c, (e) -> {
				if (logger.isTraceEnabled()) {
					logger.trace(e, "This class[{}] cannot be included because:", c.getName());
				} else if (logger.isDebugEnabled()) {
					logger.debug("This class[{}] cannot be included because {}: {}", c.getName(),
							NestedExceptionUtils.getRootCause(e).getClass(),
							NestedExceptionUtils.getNonEmptyMessage(e, false));
				}
				return false;
			}) && (predicate == null || predicate.test(c));
		}).collect(Collectors.toSet());
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(getClassLoaderProvider());
	}
}
