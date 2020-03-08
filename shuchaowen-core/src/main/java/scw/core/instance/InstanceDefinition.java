package scw.core.instance;

public interface InstanceDefinition {
	/**
	 * 是否可以调用create()方法实例化
	 * @return
	 */
	boolean isInstance();
	
	<T> T create() throws Exception;

	<T> T create(Object... params) throws Exception;

	<T> T create(Class<?>[] parameterTypes, Object... params) throws Exception;
}
