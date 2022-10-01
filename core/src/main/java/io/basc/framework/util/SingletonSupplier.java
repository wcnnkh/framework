package io.basc.framework.util;

import java.util.function.Supplier;

public class SingletonSupplier<T> implements Supplier<T> {
	private T instance;

	public SingletonSupplier(T instance) {
		this.instance = instance;
	}

	public T get() {
		return instance;
	}

}
