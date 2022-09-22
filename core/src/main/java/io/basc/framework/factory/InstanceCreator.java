package io.basc.framework.factory;

/**
 * 实例创建器
 * 
 * @author wcnnkh
 *
 * @param <T>
 * @param <E>
 */
public interface InstanceCreator<T, E extends Throwable> {
	/**
	 * 创建一个对象
	 * 
	 * @return
	 */
	T create() throws E;
	
	/**
	 * 根据参数类型和参数创建一个对象
	 * 
	 * @param parameterTypes
	 * @param params
	 * @return
	 * @throws E
	 */
	T create(Class<?>[] parameterTypes, Object[] params) throws E;
}
