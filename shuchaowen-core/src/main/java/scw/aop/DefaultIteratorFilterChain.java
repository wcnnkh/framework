package scw.aop;

import java.util.Collection;
import java.util.Iterator;

final class DefaultIteratorFilterChain extends AbstractIteratorFilterChain {
	private Iterator<? extends Filter> iterator;

	public DefaultIteratorFilterChain(Collection<? extends Filter> filters,
			FilterChain chain) {
		super(chain);
		iterator = filters.iterator();
	}

	@Override
	protected Filter getNextFilter(ProxyInvoker invoker, Object[] args)
			throws Throwable {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
