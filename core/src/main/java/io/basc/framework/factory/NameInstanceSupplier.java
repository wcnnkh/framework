package io.basc.framework.factory;

import java.util.function.Supplier;

public class NameInstanceSupplier<T> implements Supplier<T> {
	private final InstanceFactory instanceFactory;
	private final String name;

	public NameInstanceSupplier(InstanceFactory instanceFactory, String name) {
		this.instanceFactory = instanceFactory;
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public T get() {
		return (T) instanceFactory.getInstance(name);
	}

}
