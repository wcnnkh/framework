package scw.aop.cglib;

import java.lang.reflect.Method;
import java.util.Collection;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.aop.DefaultFilterChain;
import scw.aop.Filter;
import scw.aop.Invoker;

public final class CglibMethodInterceptor implements MethodInterceptor {
	private final Collection<Filter> filters;

	public CglibMethodInterceptor(Collection<Filter> filters) {
		this.filters = filters;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		DefaultFilterChain chain = new DefaultFilterChain(filters);
		Invoker invoker = new CglibInvoker(proxy, obj, args);
		return chain.doFilter(invoker, proxy, method, args);
	}
}
