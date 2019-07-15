package scw.core.instance;

public interface NoArgsInstanceFactory {
	/**
	 * 执行失败返回空或抛出异常
	 * @param type
	 * @return
	 */
	<T> T getInstance(Class<T> type);
	
	/**
	 * 执行失败返回空或抛出异常
	 * @param name
	 * @return
	 */
	<T> T getInstance(String name);
}
