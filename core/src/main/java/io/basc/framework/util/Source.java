package io.basc.framework.util;

@FunctionalInterface
public interface Source<T, E extends Throwable> {
	T get() throws E;
}
