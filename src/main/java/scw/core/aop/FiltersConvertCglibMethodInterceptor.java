package scw.core.aop;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.core.cglib.proxy.MethodInterceptor;
import scw.core.cglib.proxy.MethodProxy;

public final class FiltersConvertCglibMethodInterceptor implements MethodInterceptor {
	private final Collection<Filter> filters;

	public FiltersConvertCglibMethodInterceptor(Collection<Filter> filters) {
		this.filters = filters;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		DefaultFilterChain filterChain = new DefaultFilterChain(filters);
		return filterChain.doFilter(new CglibInvoker(proxy, obj), obj, method, args);
	}

}
