package scw.beans;

import scw.core.InstanceFactory;

public interface BeanFactory extends InstanceFactory {
	<T> T get(String name);

	<T> T get(String name, Object... params);

	<T> T get(String name, Class<?>[] parameterTypes, Object... params);

	boolean contains(String name);

	boolean isProxy(String name);
}
