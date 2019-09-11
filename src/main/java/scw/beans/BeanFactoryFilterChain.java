package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import scw.core.aop.Filter;
import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.utils.CollectionUtils;

public final class BeanFactoryFilterChain implements FilterChain {
	private Iterator<String> iterator;
	private HashSet<Filter> cache;
	private final BeanFactory beanFactory;
	private Filter lastFilter;

	public BeanFactoryFilterChain(BeanFactory beanFactory, Collection<String> filters, Filter lastFilter) {
		this.beanFactory = beanFactory;
		if (!CollectionUtils.isEmpty(filters)) {
			iterator = filters.iterator();
			cache = new HashSet<Filter>(filters.size(), 1);
			this.lastFilter = lastFilter;
		}
	}

	public Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		if (iterator == null) {
			return lastFilter(invoker, proxy, method, args);
		} else if (iterator.hasNext()) {
			Filter filter = beanFactory.getInstance(iterator.next());
			if (cache.add(filter)) {
				return filter.filter(invoker, proxy, method, args, this);
			} else {
				return doFilter(invoker, proxy, method, args);
			}
		} else {
			return lastFilter(invoker, proxy, method, args);
		}
	}

	private Object lastFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		Object value = lastFilter == null ? invoker.invoke(args)
				: lastFilter.filter(invoker, proxy, method, args, this);
		lastFilter = null;
		return value;
	}
}
