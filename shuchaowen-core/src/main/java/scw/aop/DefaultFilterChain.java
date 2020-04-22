package scw.aop;

import java.util.Collection;
import java.util.Iterator;

public final class DefaultFilterChain extends AbstractFilterChain {
	private Iterator<? extends Filter> iterator;

	public DefaultFilterChain(Collection<? extends Filter> filters) {
		this(filters, null);
	}

	public DefaultFilterChain(Collection<? extends Filter> filters, FilterChain chain) {
		super(chain);
		if (filters != null && !filters.isEmpty()) {
			iterator = filters.iterator();
		}
	}

	@Override
	protected Filter getNextFilter(Invoker invoker, Context context)
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
