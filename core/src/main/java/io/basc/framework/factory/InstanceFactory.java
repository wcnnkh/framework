package io.basc.framework.factory;

import io.basc.framework.util.ClassLoaderProvider;

public interface InstanceFactory extends ClassLoaderProvider {

	<T> T getInstance(Class<? extends T> clazz) throws FactoryException;

	Object getInstance(String name) throws FactoryException;

	boolean isInstance(Class<?> clazz);

	boolean isInstance(String name);
}
