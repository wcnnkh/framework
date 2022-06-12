package io.basc.framework.factory.supplier;

import java.util.function.Supplier;

public class SimpleInstanceSupplier<T> implements Supplier<T> {
	private T instance;

	public SimpleInstanceSupplier(T instance) {
		this.instance = instance;
	}

	public T get() {
		return instance;
	}

}
