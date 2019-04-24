package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.common.utils.CollectionUtils;
import scw.reflect.Invoker;

public final class BeanFactoryFilterChain implements FilterChain {
	private Iterator<String> iterator;
	private HashSet<Filter> cache;
	private final BeanFactory beanFactory;

	public BeanFactoryFilterChain(BeanFactory beanFactory, Collection<String> filters) {
		this.beanFactory = beanFactory;
		if (!CollectionUtils.isEmpty(filters)) {
			iterator = filters.iterator();
			cache = new HashSet<Filter>(filters.size(), 1);
		}
	}

	public Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		if (iterator == null) {
			return invoker.invoke(args);
		} else if (iterator.hasNext()) {
			Filter filter = beanFactory.get(iterator.next());
			if (cache.add(filter)) {
				return filter.filter(invoker, proxy, method, args, this);
			} else {
				return doFilter(invoker, proxy, method, args);
			}
		} else {
			return invoker.invoke(args);
		}
	}

}
