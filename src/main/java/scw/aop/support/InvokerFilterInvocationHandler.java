package scw.aop.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.aop.DefaultFilterChain;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.reflect.Invoker;

public final class InvokerFilterInvocationHandler implements InvocationHandler {
	private final Collection<Filter> filters;
	private final Invoker invoker;

	public InvokerFilterInvocationHandler(Invoker invoker, Collection<Filter> filters) {
		this.invoker = invoker;
		this.filters = filters;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		FilterChain filterChain = new DefaultFilterChain(filters);
		return filterChain.doFilter(invoker, proxy, method, args);
	}

}
