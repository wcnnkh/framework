package io.basc.framework.core.execution.aop;

import io.basc.framework.lang.Nullable;

/**
 * 代理
 * 
 * @author wcnnkh
 *
 */
public interface ProxyFactory {
	/**
	 * 是否可以代理
	 * 
	 * @param sourceClass
	 * @return
	 */
	boolean canProxy(Class<?> sourceClass);

	/**
	 * 获取执行后可获取代理的对象
	 * 
	 * @param sourceClass
	 * @param interfaces
	 * @param executionInterceptor
	 * @return
	 */
	Proxy getProxy(Class<?> sourceClass, @Nullable Class<?>[] interfaces, ExecutionInterceptor executionInterceptor);

	/**
	 * 获取代理类
	 * 
	 * @param sourceClass
	 * @param interfaces
	 * @return
	 */
	Class<?> getProxyClass(Class<?> sourceClass, Class<?>[] interfaces);

	/**
	 * 获取未被代理的原始类型
	 * 
	 * @param proxyClass
	 * @return 原始类
	 */
	Class<?> getUserClass(Class<?> proxyClass);

	/**
	 * 获取用户类
	 * 
	 * @param proxyClassName proxy class name
	 * @param classLoader    class load for name
	 * @return 返回被代理的原始类
	 * @throws ClassNotFoundException 类不存在
	 */
	Class<?> getUserClass(String proxyClassName, @Nullable ClassLoader classLoader) throws ClassNotFoundException;

	/**
	 * 是否是代理类
	 * 
	 * @param proxyClass
	 * @return
	 */
	boolean isProxy(Class<?> proxyClass);

	/**
	 * 是否是一个代理类
	 * 
	 * @param proxyClassName proxy class name
	 * @param classLoader    class load for name
	 * @return 是否已被代理
	 * @throws ClassNotFoundException 类不存在
	 */
	boolean isProxy(String proxyClassName, @Nullable ClassLoader classLoader) throws ClassNotFoundException;
}
