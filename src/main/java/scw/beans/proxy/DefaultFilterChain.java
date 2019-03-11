package scw.beans.proxy;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

public final class DefaultFilterChain implements FilterChain {
	private Iterator<Filter> iterator;

	public DefaultFilterChain(Collection<Filter> filters) {
		if (filters != null && !filters.isEmpty()) {
			iterator = filters.iterator();
		}
	}

	public Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args, FilterChain chain)
			throws Throwable {
		if (iterator != null && iterator.hasNext()) {
			return iterator.next().filter(invoker, proxy, method, args, chain);
		} else {
			return invoker.invoke();
		}
	}
}
