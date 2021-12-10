package io.basc.framework.util;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface Supplier<T> extends java.util.function.Supplier<T>, Callable<T> {
	T get();

	@Override
	default T call() {
		return get();
	}
}
