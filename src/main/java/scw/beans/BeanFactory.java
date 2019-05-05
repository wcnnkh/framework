package scw.beans;

import scw.core.instance.InstanceFactory;

public interface BeanFactory extends InstanceFactory{
	<T> T get(String name);

	<T> T get(String name, Object ...params);
	
	<T> T get(Class<T> type, Object ...params);
	
	<T> T get(String name, Class<?>[] parameterTypes, Object ...params);
	
	<T> T get(Class<T> type, Class<?>[] parameterTypes, Object ...params);
	
	boolean contains(String name);
}
