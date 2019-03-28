package scw.aop;

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

	public Object doFilter(Invoker invoker, Object proxy, Method method, Object[] args)
			throws Throwable {
		if(iterator == null){
			return invoker.invoke(args);
		}
		
		if (iterator.hasNext()) {
			return iterator.next().filter(invoker, proxy, method, args, this);
		} else {
			return invoker.invoke(args);
		}
	}
}
