package scw.aop;

/**
 * 代理桥接器
 * 
 * @author shuchaowen
 *
 */
public interface ProxyAdapter {
	/**
	 * 是否支持此类(桥接器)
	 * 
	 * @param clazz
	 * @return
	 */
	boolean isSupport(Class<?> clazz);

	Proxy proxy(Class<?> clazz, Class<?>[] interfaces, FilterChain filterChain);

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

	/**
	 * 获取未被代理的原始类型
	 * 
	 * @param clazz
	 * @return
	 */
	Class<?> getUserClass(Class<?> clazz);

	boolean isProxy(String className, ClassLoader classLoader);

	Class<?> getUserClass(String className, boolean initialize, ClassLoader classLoader) throws ClassNotFoundException;
}