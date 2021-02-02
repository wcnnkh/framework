package scw.aop;

import scw.lang.Nullable;

/**
 * 代理桥接器
 * 
 * @author shuchaowen
 *
 */
public interface ProxyFactory {
	/**
	 * 是否支持此类(桥接器)
	 * 
	 * @param clazz
	 * @return
	 */
	boolean canProxy(Class<?> clazz);
	
	/**
	 * 是否是代理类
	 * 
	 * @param clazz
	 * @return
	 */
	boolean isProxy(Class<?> clazz);

	/**
	 * 获取未被代理的原始类型
	 * 
	 * @param clazz
	 * @return
	 */
	Class<?> getUserClass(Class<?> clazz);
	
	/**
	 * 获取代理类
	 * 
	 * @param interfaceClass
	 * @return
	 */
	Class<?> getProxyClass(Class<?> clazz, @Nullable Class<?>[] interfaces);

	Proxy getProxy(Class<?> clazz, @Nullable Class<?>[] interfaces, @Nullable MethodInterceptor methodInterceptor);
	
	boolean isProxy(String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException;
	
	Class<?> getUserClass(String className, @Nullable ClassLoader classLoader)
			throws ClassNotFoundException;
}