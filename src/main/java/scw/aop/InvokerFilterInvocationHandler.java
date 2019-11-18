package scw.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

public final class InvokerFilterInvocationHandler implements InvocationHandler {
	private final Collection<Filter> filters;
	private final Invoker invoker;
	private final Class<?> targetClass;

	public InvokerFilterInvocationHandler(Invoker invoker, Class<?> targetClass, Collection<Filter> filters) {
		this.invoker = invoker;
		this.filters = filters;
		this.targetClass = targetClass;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		FilterChain filterChain = new DefaultFilterChain(filters);
		return filterChain.doFilter(invoker == null ? new ReflectInvoker(proxy, method) : invoker, proxy, targetClass,
				method, args);
	}

}
