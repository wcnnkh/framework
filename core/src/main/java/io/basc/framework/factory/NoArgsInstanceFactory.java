package io.basc.framework.factory;

import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Supplier;

public interface NoArgsInstanceFactory extends ClassLoaderProvider{
	default <T> T getInstance(String name){
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) ClassUtils.getClass(name, getClassLoader());
		return getInstance(clazz);
	}
	
	default <T> Supplier<T> getInstanceSupplier(String name){
		return new Supplier<T>() {
			@Override
			public T get() {
				return getInstance(name);
			}
		};
	}
	
	<T> T getInstance(Class<T> clazz);
	
	default <T> Supplier<T> getInstanceSupplier(Class<T> clazz){
		return new Supplier<T>() {
			@Override
			public T get() {
				return getInstance(clazz);
			}
		};
	}

	/**
	 * 表示可以使用getInstance(String name)方式获取实例
	 * 
	 * @param name
	 * @return
	 */
	default boolean isInstance(String name){
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
