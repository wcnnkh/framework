package scw.core.instance;

public interface NoArgsInstanceFactory {
	/**
	 * 是否是单例
	 * @param name
	 * @return
	 */
	boolean isSingleton(String name);

	/**
	 * 是否是单例
	 * @param clazz
	 * @return
	 */
	boolean isSingleton(Class<?> clazz);
	
	/**
	 * 执行失败返回空
	 * 
	 * @param type
	 * @return
	 */
	<T> T getInstance(Class<T> type);

	/**
	 * 执行失败返回空
	 * 
	 * @param name
	 * @return
	 */
	<T> T getInstance(String name);

	/**
	 * 表示可以使用getInstance(String name)方式获取实例
	 * 
	 * @param name
	 * @return
	 */
	boolean isInstance(String name);

	/**
	 * 表示可以使用getInstance(Class<T> type)方式获取实例
	 * 
	 * @param clazz
	 * @return
	 */
	boolean isInstance(Class<?> clazz);
}
