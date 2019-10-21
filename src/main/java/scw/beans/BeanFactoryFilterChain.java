package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.InstanceFactoryFilterChain;
import scw.core.aop.Invoker;

public final class BeanFactoryFilterChain implements FilterChain {
	private final FilterChain filterChain;

	public BeanFactoryFilterChain(BeanFactory beanFactory, Collection<String> filterNames, Class<?> clz, Method method,
			Filter lastFilter) {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(beanFactory.getFilterNames());
		if (filterNames != null) {
			list.addAll(filterNames);
		}

		scw.beans.annotation.BeanFilter beanFilter = clz.getAnnotation(scw.beans.annotation.BeanFilter.class);
		if (beanFilter != null) {
			for (Class<? extends Filter> c : beanFilter.value()) {
				list.add(c.getName());
			}
		}

		beanFilter = method.getAnnotation(scw.beans.annotation.BeanFilter.class);
		if (beanFilter != null) {
			for (Class<? extends Filter> c : beanFilter.value()) {
				list.add(c.getName());
			}
		}

		this.filterChain = new InstanceFactoryFilterChain(beanFactory, list, lastFilter);

	}

	public Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		return filterChain.doFilter(invoker, proxy, method, args);
	}
}
