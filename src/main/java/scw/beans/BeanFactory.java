package scw.beans;

import scw.core.InstanceFactory;

public interface BeanFactory extends InstanceFactory {
	<T> T getInstance(String name);
	
	<T> T getInstance(String name, Object... params);
	
	<T> T getInstance(Class<T> type, Object ...params);

	<T> T getInstance(String name, Class<?>[] parameterTypes, Object... params);
	
	<T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object... params);

	boolean contains(String name);

	boolean isProxy(String name);
}
