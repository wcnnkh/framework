package scw.aop;

import java.util.Collection;
import java.util.Iterator;

import scw.aop.Context;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;

final class DefaultIteratorFilterChain extends AbstractIteratorFilterChain {
	private Iterator<? extends Filter> iterator;

	public DefaultIteratorFilterChain(Collection<? extends Filter> filters,
			FilterChain chain) {
		super(chain);
		iterator = filters.iterator();
	}

	@Override
	protected Filter getNextFilter(Invoker invoker, Context context)
			throws Throwable {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
