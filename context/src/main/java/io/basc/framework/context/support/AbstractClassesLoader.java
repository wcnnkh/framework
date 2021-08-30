package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.core.type.scanner.ClassResolver;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;

import java.util.Iterator;
import java.util.Set;

public abstract class AbstractClassesLoader extends ClassResolver implements
		ClassesLoader, ClassLoaderProvider {
	private volatile Set<Class<?>> classes;
	private ClassLoaderProvider classLoaderProvider;

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
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

	protected abstract Set<Class<?>> getClasses(ClassLoader classLoader);

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(getClassLoaderProvider());
	}
}
