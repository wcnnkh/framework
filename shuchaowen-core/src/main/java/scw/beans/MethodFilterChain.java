package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.aop.DefaultFilterChain;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.InstanceFactoryFilterChain;
import scw.aop.Invoker;

public final class MethodFilterChain implements FilterChain {
	private final FilterChain filterChain;

	public MethodFilterChain(BeanFactory beanFactory, Class<?> clz, Method method, Collection<String> filterNames,
			Collection<Filter> filters) {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(beanFactory.getFilterNames());
		this.filterChain = new InstanceFactoryFilterChain(beanFactory, list, new DefaultFilterChain(filters));
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args)
			throws Throwable {
		return filterChain.doFilter(invoker, proxy, targetClass, method, args);
	}
}
