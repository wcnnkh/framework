package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.invoke.intercept.ExecutionInterceptor;

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
	boolean canProxy(@NonNull Class<?> sourceClass);

	/**
	 * 获取执行后可获取代理的对象
	 * 
	 * @param sourceClass
	 * @param interfaces
	 * @param executionInterceptor
	 * @return
	 */
	Proxy getProxy(@NonNull Class<?> sourceClass, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor);

	/**
	 * 获取代理类
	 * 
	 * @param sourceClass
	 * @param interfaces
	 * @return
	 */
	Class<?> getProxyClass(@NonNull Class<?> sourceClass, Class<?>[] interfaces);

	/**
	 * 获取未被代理的原始类型
	 * 
	 * @param proxyClass
	 * @return 原始类
	 */
	Class<?> getUserClass(@NonNull Class<?> proxyClass);

	/**
	 * 获取用户类
	 * 
	 * @param proxyClassName proxy class name
	 * @param classLoader    class load for name
	 * @return 返回被代理的原始类
	 * @throws ClassNotFoundException 类不存在
	 */
	Class<?> getUserClass(@NonNull String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException;

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
	boolean isProxy(@NonNull String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException;
}
