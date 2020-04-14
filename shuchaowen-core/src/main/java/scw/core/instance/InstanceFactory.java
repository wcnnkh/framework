package scw.core.instance;

import scw.core.instance.definition.InstanceDefinition;

public interface InstanceFactory extends NoArgsInstanceFactory {
	InstanceDefinition getDefinition(String name);
	
	InstanceDefinition getDefinition(Class<?> clazz);
	
	/**
	 * 执行失败返回空
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	<T> T getInstance(String name, Object... params);

	/**
	 * 执行失败返回空
	 * 
	 * @param type
	 * @param params
	 * @return
	 */
	<T> T getInstance(Class<? extends T> type, Object... params);

	/**
	 * 执行失败返回空
	 * 
	 * @param name
	 * @param parameterTypes
	 * @param params
	 * @return
	 */
	<T> T getInstance(String name, Class<?>[] parameterTypes, Object... params);

	/**
	 * 执行失败返回空
	 * 
	 * @param type
	 * @param parameterTypes
	 * @param params
	 * @return
	 */
	<T> T getInstance(Class<? extends T> type, Class<?>[] parameterTypes,
			Object... params);
}
