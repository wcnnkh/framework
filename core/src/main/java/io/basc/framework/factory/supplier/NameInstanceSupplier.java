package io.basc.framework.factory.supplier;

import java.util.function.Supplier;

import io.basc.framework.factory.NoArgsInstanceFactory;

public class NameInstanceSupplier<T> implements Supplier<T> {
	private final NoArgsInstanceFactory instanceFactory;
	private final String name;

	public NameInstanceSupplier(NoArgsInstanceFactory instanceFactory, String name) {
		this.instanceFactory = instanceFactory;
		this.name = name;
	}

	public T get() {
		return instanceFactory.getInstance(name);
	}

}
