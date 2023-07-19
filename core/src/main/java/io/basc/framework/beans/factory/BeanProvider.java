package io.basc.framework.beans.factory;

import java.util.Optional;

import io.basc.framework.util.ServiceLoader;

public interface BeanProvider<T> extends ServiceLoader<T> {
	boolean isUnique();

	Optional<T> getUnique();

	boolean isEmpty();

	Optional<T> findFirst();
}
