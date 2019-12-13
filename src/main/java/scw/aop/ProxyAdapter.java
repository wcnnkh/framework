package scw.aop;

import java.util.Collection;

/**
 * 代理桥接器
 * 
 * @author shuchaowen
 *
 */
public interface ProxyAdapter {
	/**
	 * 是否支持代理
	 * 
	 * @param clazz
	 * @return
	 */
	boolean isSupport(Class<?> clazz);

	Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters);

	Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters, FilterChain filterChain);

	/**
	 * 获取代理类
	 * 
	 * @param interfaceClass
	 * @return
	 */
	Class<?> getClass(Class<?> clazz, Class<?>[] interfaces);

	/**
	 * 是否是代理类
	 * 
	 * @param clazz
	 * @return
	 */
	boolean isProxy(Class<?> clazz);
}