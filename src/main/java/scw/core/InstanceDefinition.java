package scw.core;

public interface InstanceDefinition {
	/**
	 * 是否可以调用create()方法实例化
	 * @return
	 */
	boolean isInstance();
	
	<T> T create();

	<T> T create(Object... params);

	<T> T create(Class<?>[] parameterTypes, Object... params);
}
