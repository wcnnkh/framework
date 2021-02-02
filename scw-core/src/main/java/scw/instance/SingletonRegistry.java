package scw.instance;

import scw.util.Creator;
import scw.util.Result;


public interface SingletonRegistry extends SingletonFactory{
	void registerSingleton(String name, Object singletonObject);
	
	void removeSingleton(String name);
	
	<T> Result<T> getSingleton(String name, Creator<T> creater);
	
	Object getSingletonMutex();
}