package io.basc.framework.beans;

import io.basc.framework.util.Creator;
import io.basc.framework.util.Return;

public interface SingletonRegistry extends SingletonFactory {
	void registerSingleton(String name, Object singletonObject);

	<T, E extends Throwable> Return<T> getSingleton(String name, Creator<? extends T, ? extends E> creater) throws E;

	Object getSingletonMutex();
}