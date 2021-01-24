package scw.context.support;

import java.util.Iterator;
import java.util.Set;

import scw.context.ClassesLoader;
import scw.core.utils.ClassUtils;
import scw.util.ClassLoaderProvider;

public abstract class AbstractClassesLoader<S> extends ClassResolver implements
		ClassesLoader<S>, ClassLoaderProvider {
	private volatile Set<Class<S>> classes;
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

	public Iterator<Class<S>> iterator() {
		if (classes == null) {
			synchronized (this) {
				if (classes == null) {
					classes = getClasses(getClassLoader());
				}
			}
		}
		return classes.iterator();
	}

	protected abstract Set<Class<S>> getClasses(ClassLoader classLoader);

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(getClassLoaderProvider());
	}
}
