package scw.aop;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

public final class DefaultFilterChain extends AbstractFilterChain {
	private Iterator<Filter> iterator;

	public DefaultFilterChain(Collection<Filter> filters) {
		this(filters, null);
	}

	public DefaultFilterChain(Collection<Filter> filters, FilterChain chain) {
		super(chain);
		if (filters != null && !filters.isEmpty()) {
			iterator = filters.iterator();
		}
	}

	@Override
	protected Filter getNextFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args)
			throws Throwable {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
