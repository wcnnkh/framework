package io.basc.framework.aop;

import io.basc.framework.lang.Nullable;

/**
 * 代理工厂
 * 
 * @author wcnnkh
 *
 */
public interface ProxyFactory {
	/**
	 * 是否可以代理
	 * 
	 * @param clazz origin class
	 * @return yes/no
	 */
	boolean canProxy(Class<?> clazz);

	/**
	 * 是否是代理类
	 * 
	 * @param clazz proxy class
	 * @return yes/no
	 */
	boolean isProxy(Class<?> clazz);

	/**
	 * 获取未被代理的原始类型
	 * 
	 * @param clazz proxy class
	 * @return 返回原始类
	 */
	Class<?> getUserClass(Class<?> clazz);

	/**
	 * 获取代理
	 * 
	 * @param clazz             source class
	 * @param interfaces        interfaces
	 * @param methodInterceptor interceptors
	 * @return 返回代理
	 */
	Proxy getProxy(Class<?> clazz, @Nullable Class<?>[] interfaces, @Nullable MethodInterceptor methodInterceptor);

	/**
	 * 是否是一个代理类
	 * 
	 * @param className   proxy class name
	 * @param classLoader class load for name
	 * @return 是否已被代理
	 * @throws ClassNotFoundException 类不存在
	 */
	boolean isProxy(String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException;

	/**
	 * 获取用户类
	 * 
	 * @param className   proxy class name
	 * @param classLoader class load for name
	 * @return 返回被代理的原始类
	 * @throws ClassNotFoundException 类不存在
	 */
	Class<?> getUserClass(String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException;
}