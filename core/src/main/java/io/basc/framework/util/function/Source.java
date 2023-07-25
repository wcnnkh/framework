package io.basc.framework.util.function;

@FunctionalInterface
public interface Source<T, E extends Throwable> {
	T get() throws E;
}
