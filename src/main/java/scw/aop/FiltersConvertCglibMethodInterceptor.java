package scw.aop;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.core.cglib.proxy.MethodInterceptor;
import scw.core.cglib.proxy.MethodProxy;

public final class FiltersConvertCglibMethodInterceptor implements MethodInterceptor {
	private final Collection<Filter> filters;
	private final Class<?> targetClass;

	public FiltersConvertCglibMethodInterceptor(Class<?> targetClass, Collection<Filter> filters) {
		this.filters = filters;
		this.targetClass = targetClass;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		DefaultFilterChain filterChain = new DefaultFilterChain(filters);
		return filterChain.doFilter(new CglibInvoker(proxy, obj), obj, targetClass, method, args);
	}

}
