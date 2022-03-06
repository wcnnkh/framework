package io.basc.framework.factory;

import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;

public interface NoArgsInstanceFactory extends ClassLoaderProvider {

	/**
	 * 根据名称获取一个实例
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 */
	default <T> T getInstance(String name) {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) ClassUtils.getClass(name, getClassLoader());
		return getInstance(clazz);
	}

	/**
	 * 获取一个实例
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	<T> T getInstance(Class<T> clazz);

	/**
	 * 表示可以使用getInstance(String name)方式获取实例
	 * 
	 * @param name
	 * @return
	 */
	default boolean isInstance(String name) {
		return isInstance(ClassUtils.getClass(name, getClassLoader()));
	}

	/**
	 * 表示可以使用getInstance(Class<T> type)方式获取实例
	 * 
	 * @param clazz
	 * @return
	 */
	boolean isInstance(Class<?> clazz);
}
