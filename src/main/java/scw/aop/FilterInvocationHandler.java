package scw.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

public final class FilterInvocationHandler implements InvocationHandler {
	private final Collection<Filter> filters;
	private final Object obj;
	private final Class<?> targetClass;

	public FilterInvocationHandler(Class<?> targetClass, Collection<Filter> filters) {
		this(targetClass, null, filters);
	}

	public FilterInvocationHandler(Class<?> targetClass, Object obj, Collection<Filter> filters) {
		this.obj = obj;
		this.targetClass = targetClass;
		this.filters = filters;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		FilterChain filterChain = new DefaultFilterChain(filters);
		return filterChain.doFilter(new ReflectInvoker(obj == null ? proxy : obj, method), proxy, targetClass, method,
				args);
	}

}
