package io.basc.framework.generator;

@FunctionalInterface
public interface Generator<T> {
	T next();
}
