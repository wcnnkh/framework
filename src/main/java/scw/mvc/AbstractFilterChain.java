package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;

public abstract class AbstractFilterChain implements FilterChain {
	private Iterator<Filter> iterator;

	public AbstractFilterChain(Collection<Filter> filters) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
	}

	public Object doFilter(Channel channel) throws Throwable {
		if (iterator == null) {
			return lastFilter(channel);
		}

		if (iterator.hasNext()) {
			return iterator.next().doFilter(channel, this);
		}

		return lastFilter(channel);
	}

	protected abstract Object lastFilter(Channel channel) throws Throwable;
}
