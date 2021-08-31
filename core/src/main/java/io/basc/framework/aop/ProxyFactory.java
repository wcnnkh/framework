package io.basc.framework.aop;

import io.basc.framework.lang.Nullable;

/**
 * 代理工厂
 * 
 * @author shuchaowen
 *
 */
public interface ProxyFactory {
	/**
	  *  是否可以代理
	 * 
	 * @param clazz origin class
	 * @return
	 */
	boolean canProxy(Class<?> clazz);
	
	/**
	  *  是否是代理类
	 * 
	 * @param clazz proxy class
	 * @return
	 */
	boolean isProxy(Class<?> clazz);

	/**
	  *  获取未被代理的原始类型
	 * 
	 * @param clazz proxy class
	 * @return
	 */
	Class<?> getUserClass(Class<?> clazz);
	
	/**
	  *  获取代理类
	 * 
	 * @param clazz source class
	 * @param interfaceClass interfaces
	 * @return
	 */
	Class<?> getProxyClass(Class<?> clazz, @Nullable Class<?>[] interfaces);

	/**
	  *  获取代理
	 * @param clazz source class
	 * @param interfaces interfaces
	 * @param methodInterceptor interceptors
	 * @return
	 */
	Proxy getProxy(Class<?> clazz, @Nullable Class<?>[] interfaces, @Nullable MethodInterceptor methodInterceptor);
	
	/**
	  *  是否是一个代理类
	 * @param className proxy class name
	 * @param classLoader class load for name
	 * @return
	 * @throws ClassNotFoundException
	 */
	boolean isProxy(String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException;
	
	/**
	  *  获取用户类
	 * @param className proxy class name
	 * @param classLoader class load for name
	 * @return
	 * @throws ClassNotFoundException
	 */
	Class<?> getUserClass(String className, @Nullable ClassLoader classLoader)
			throws ClassNotFoundException;
}