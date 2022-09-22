package io.basc.framework.factory;

import io.basc.framework.util.Status;
import io.basc.framework.util.stream.CallableProcessor;

public interface SingletonRegistry extends SingletonFactory {
	void registerSingleton(String name, Object singletonObject);

	void removeSingleton(String name);

	<T, E extends Throwable> Status<T> getSingleton(String name, CallableProcessor<T, E> creater) throws E;

	Object getSingletonMutex();
}