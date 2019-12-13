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
		scw.beans.annotation.Filters annotationFilters = clz.getAnnotation(scw.beans.annotation.Filters.class);
		if (annotationFilters != null) {
			for (String n : annotationFilters.names()) {
				list.add(n);
			}

			for (Class<? extends Filter> c : annotationFilters.value()) {
				list.add(c.getName());
			}
		}

		annotationFilters = method.getAnnotation(scw.beans.annotation.Filters.class);
		if (annotationFilters != null) {
			for (String n : annotationFilters.names()) {
				list.add(n);
			}

			for (Class<? extends Filter> c : annotationFilters.value()) {
				list.add(c.getName());
			}
		}

		if (filterNames != null) {
			list.addAll(filterNames);
		}
		this.filterChain = new InstanceFactoryFilterChain(beanFactory, list, new DefaultFilterChain(filters));
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args)
			throws Throwable {
		return filterChain.doFilter(invoker, proxy, targetClass, method, args);
	}
}
