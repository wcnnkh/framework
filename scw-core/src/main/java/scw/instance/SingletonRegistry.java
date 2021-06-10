package scw.instance;

import scw.util.Creator;
import scw.util.Result;

public interface SingletonRegistry extends SingletonFactory {
	void registerSingleton(String name, Object singletonObject);

	void removeSingleton(String name);

	<T, E extends Throwable> Result<T> getSingleton(String name, Creator<T, E> creater) throws E;

	Object getSingletonMutex();
}