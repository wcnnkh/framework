package io.basc.framework.factory;

import io.basc.framework.util.ClassLoaderProvider;

public interface InstanceFactory extends ClassLoaderProvider {

	/**
	 * 获取一个实例
	 * 
	 * @param       <T>
	 * @param clazz
	 * @return
	 */
	<T> T getInstance(Class<? extends T> clazz) throws FactoryException;

	/**
	 * 根据名称获取一个实例
	 * 
	 * @param
	 * @param name
	 * @return
	 */
	Object getInstance(String name) throws FactoryException;

	/**
	 * 表示可以使用getInstance(Class<T> type)方式获取实例
	 * 
	 * @param clazz
	 * @return
	 */
	boolean isInstance(Class<?> clazz);

	/**
	 * 表示可以使用getInstance(String name)方式获取实例
	 * 
	 * @param name
	 * @return
	 */
	boolean isInstance(String name);
}
