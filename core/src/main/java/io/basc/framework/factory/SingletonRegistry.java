package io.basc.framework.factory;

import io.basc.framework.util.Status;

public interface SingletonRegistry extends SingletonFactory {
	void registerSingleton(String name, Object singletonObject);

	void removeSingleton(String name);

	<T, E extends Throwable> Status<T> getSingleton(String name, Factory<T, E> creater) throws E;

	Object getSingletonMutex();
}