package scw.aop;

import scw.lang.Nullable;

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
	 * @param clazz
	 * @return
	 */
	boolean canProxy(Class<?> clazz);
	
	/**
	  *  是否是代理类
	 * 
	 * @param clazz
	 * @return
	 */
	boolean isProxy(Class<?> clazz);

	/**
	  *  获取未被代理的原始类型
	 * 
	 * @param clazz
	 * @return
	 */
	Class<?> getUserClass(Class<?> clazz);
	
	/**
	  *  获取代理类
	 * 
	 * @param interfaceClass
	 * @return
	 */
	Class<?> getProxyClass(Class<?> clazz, @Nullable Class<?>[] interfaces);

	/**
	  *  获取代理
	 * @param clazz
	 * @param interfaces
	 * @param methodInterceptor
	 * @return
	 */
	Proxy getProxy(Class<?> clazz, @Nullable Class<?>[] interfaces, @Nullable MethodInterceptor methodInterceptor);
	
	/**
	  *  是否是一个代理类
	 * @param className
	 * @param classLoader
	 * @return
	 * @throws ClassNotFoundException
	 */
	boolean isProxy(String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException;
	
	/**
	  *  获取用户类
	 * @param className
	 * @param classLoader
	 * @return
	 * @throws ClassNotFoundException
	 */
	Class<?> getUserClass(String className, @Nullable ClassLoader classLoader)
			throws ClassNotFoundException;
}