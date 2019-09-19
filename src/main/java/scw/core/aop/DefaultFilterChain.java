package scw.core.aop;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

public final class DefaultFilterChain implements FilterChain {
	private Iterator<Filter> iterator;
	private FilterChain filterChain;

	public DefaultFilterChain(Collection<Filter> filters) {
		this(filters, null);
	}

	public DefaultFilterChain(Collection<Filter> filters, FilterChain filterChain) {
		if (filters != null && !filters.isEmpty()) {
			iterator = filters.iterator();
		}
		this.filterChain = filterChain;
	}

	public Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		if (iterator == null) {
			return lastFilter(invoker, proxy, method, args);
		}

		if (iterator.hasNext()) {
			return iterator.next().filter(invoker, proxy, method, args, this);
		} else {
			return lastFilter(invoker, proxy, method, args);
		}
	}

	public Object lastFilter(Invoker invoker, Object proxy, Method method, Object[] args) throws Throwable {
		return filterChain == null ? invoker.invoke(args) : filterChain.doFilter(invoker, proxy, method, args);
	}
}
