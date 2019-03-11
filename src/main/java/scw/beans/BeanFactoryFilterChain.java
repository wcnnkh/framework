package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import scw.beans.proxy.Filter;
import scw.beans.proxy.FilterChain;
import scw.beans.proxy.Invoker;

public final class BeanFactoryFilterChain implements FilterChain {
	private BeanFactory beanFactory;
	private Iterator<String> iterator;
	private HashSet<Filter> cacheMap;

	public BeanFactoryFilterChain(BeanFactory beanFactory, Collection<String> collection) {
		this.beanFactory = beanFactory;
		if (collection != null && !collection.isEmpty()) {
			this.iterator = collection.iterator();
			cacheMap = new HashSet<Filter>(collection.size(), 1);
		}
	}

	public Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		if (iterator == null) {
			return invoker.invoke();
		}

		if (iterator.hasNext()) {
			Filter filter = beanFactory.get(iterator.next());
			if (cacheMap.add(filter)) {
				return filter.filter(invoker, proxy, method, args, this);
			} else {
				return doFilter(invoker, proxy, method, args);
			}
		} else {
			return invoker.invoke();
		}
	}

}
