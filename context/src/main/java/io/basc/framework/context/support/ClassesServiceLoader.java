package io.basc.framework.context.support;

import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.support.ClassInstanceIterator;
import io.basc.framework.util.ClassesLoader;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.ServiceLoader;

public class ClassesServiceLoader<S> implements ServiceLoader<S> {
	private final ClassesLoader classesLoader;
	private final InstanceFactory instanceFactory;

	public ClassesServiceLoader(ClassesLoader classesLoader, InstanceFactory instanceFactory) {
		this.classesLoader = classesLoader;
		this.instanceFactory = instanceFactory;
	}

	@Override
	public void reload() {
	}

	@Override
	public Cursor<S> iterator() {
		return Cursor.of(new ClassInstanceIterator<>(instanceFactory, classesLoader.iterator()));
	}
}
