package io.basc.framework.context.support;

import java.util.Iterator;

import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.support.ClassInstanceIterator;
import io.basc.framework.util.ServiceLoader;

public class ClassesServiceLoader<S> implements ServiceLoader<S> {
	private final ServiceLoader<Class<?>> classesLoader;
	private final InstanceFactory instanceFactory;

	public ClassesServiceLoader(ServiceLoader<Class<?>> classesLoader, InstanceFactory instanceFactory) {
		this.classesLoader = classesLoader;
		this.instanceFactory = instanceFactory;
	}

	@Override
	public void reload() {
	}

	@Override
	public Iterator<S> iterator() {
		return new ClassInstanceIterator<>(instanceFactory, classesLoader.iterator());
	}
}
