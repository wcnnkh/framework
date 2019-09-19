package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.core.aop.CglibInvoker;
import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.cglib.proxy.MethodInterceptor;
import scw.core.cglib.proxy.MethodProxy;

public final class RootFilter implements Filter, MethodInterceptor {
	private BeanFactory beanFactory;
	private Filter lastFilter;
	private Collection<String> filterNames;

	public RootFilter(BeanFactory beanFactory, Collection<String> filterNames, Filter lastFilter) {
		this.beanFactory = beanFactory;
		this.lastFilter = lastFilter;
		this.filterNames = filterNames;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (obj instanceof Filter) {
			return proxy.invokeSuper(obj, args);
		}

		Invoker invoker = new CglibInvoker(proxy, obj);
		return invoke(invoker, obj, method, args);
	}

	private Object invoke(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		FilterChain chain = new BeanFactoryFilterChain(beanFactory, filterNames, method.getDeclaringClass(), method,
				lastFilter);
		return chain.doFilter(invoker, proxy, method, args);
	}

	public Object filter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain filterChain)
			throws Throwable {
		if (proxy instanceof Filter) {
			return invoker.invoke(args);
		}

		return invoke(invoker, proxy, method, args);
	}

}
