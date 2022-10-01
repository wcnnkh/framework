package io.basc.framework.factory;

import java.util.function.Supplier;

public class ClassInstanceSupplier<T> implements Supplier<T> {
	private final InstanceFactory instanceFactory;
	private final Class<T> clazz;

	public ClassInstanceSupplier(InstanceFactory instanceFactory, Class<T> clazz) {
		this.instanceFactory = instanceFactory;
		this.clazz = clazz;
	}

	public T get() {
		return instanceFactory.getInstance(clazz);
	}
}
