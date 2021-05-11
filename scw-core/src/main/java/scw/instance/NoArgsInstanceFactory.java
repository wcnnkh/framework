package scw.instance;

import scw.core.utils.ClassUtils;
import scw.util.ClassLoaderProvider;

public interface NoArgsInstanceFactory extends ClassLoaderProvider{
	default <T> T getInstance(String name){
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) ClassUtils.getClass(name, getClassLoader());
		return getInstance(clazz);
	}
	
	<T> T getInstance(Class<T> clazz);

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
