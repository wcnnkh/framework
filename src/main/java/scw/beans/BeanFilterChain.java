package scw.beans;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import net.sf.cglib.proxy.MethodProxy;

public final class BeanFilterChain {
	private Iterator<String> filterIterator;
	private BeanFactory beanFactory;

	public BeanFilterChain(BeanFactory beanFactory, Collection<String> filters) {
		if (filters != null) {
			this.filterIterator = filters.iterator();
		}
	}

	public Object doFilter(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (filterIterator != null && filterIterator.hasNext()) {
			BeanFilter beanFilter = beanFactory.get(filterIterator.next());
			return beanFilter.doFilter(obj, method, args, proxy, this);
		} else {
			return proxy.invokeSuper(obj, args);
		}
	}
}
