package scw.aop;

import java.lang.reflect.Method;

public interface Filter {
	/**
	 * AOP拦截器
	 * @param invoker
	 *            调用本身
	 * @param proxy
	 *            代理后的对象
	 * @param targetClass
	 *            原始类
	 * @param method
	 *            当前方法
	 * @param args
	 *            方法参数
	 * @param filterChain
	 *            调用链
	 * @return
	 * @throws Throwable
	 */
	Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
			FilterChain filterChain) throws Throwable;
}
