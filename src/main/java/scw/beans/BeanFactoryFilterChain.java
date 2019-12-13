package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.aop.DefaultFilterChain;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.InstanceFactoryFilterChain;
import scw.aop.Invoker;

public final class BeanFactoryFilterChain implements FilterChain {
	private final FilterChain filterChain;

	public BeanFactoryFilterChain(BeanFactory beanFactory, Collection<String> filterNames, Class<?> clz, Method method,
			Collection<Filter> filters) {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(beanFactory.getFilterNames());
		if (filterNames != null) {
			list.addAll(filterNames);
		}

		scw.beans.annotation.BeanFilter beanFilter = clz.getAnnotation(scw.beans.annotation.BeanFilter.class);
		if (beanFilter != null) {
			for (String n : beanFilter.names()) {
				list.add(n);
			}

			for (Class<? extends Filter> c : beanFilter.value()) {
				list.add(c.getName());
			}
		}

		beanFilter = method.getAnnotation(scw.beans.annotation.BeanFilter.class);
		if (beanFilter != null) {
			for (String n : beanFilter.names()) {
				list.add(n);
			}

			for (Class<? extends Filter> c : beanFilter.value()) {
				list.add(c.getName());
			}
		}

		this.filterChain = new InstanceFactoryFilterChain(beanFactory, list, new DefaultFilterChain(filters));

	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args)
			throws Throwable {
		return filterChain.doFilter(invoker, proxy, targetClass, method, args);
	}
}
