package scw.core.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

public final class FilterInvocationHandler implements InvocationHandler {
	private final Collection<Filter> filters;
	private final Object obj;

	public FilterInvocationHandler(Collection<Filter> filters) {
		this(null, filters);
	}

	public FilterInvocationHandler(Object obj, Collection<Filter> filters) {
		this.obj = obj;
		this.filters = filters;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		FilterChain filterChain = new DefaultFilterChain(filters);
		return filterChain.doFilter(new ReflectInvoker(obj == null ? proxy : obj, method), proxy, method, args);
	}

}
