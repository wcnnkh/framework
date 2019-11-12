package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;

public class SimpleFilterChain implements FilterChain {
	private Iterator<Filter> iterator;
	private FilterChain chain;

	public SimpleFilterChain(Collection<Filter> filters) {
		this(filters, null);
	}

	public SimpleFilterChain(Collection<Filter> filters, FilterChain chain) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
		this.chain = chain;
	}

	public Object doFilter(Channel channel) throws Throwable {
		if (iterator == null) {
			return chain == null ? null : chain.doFilter(channel);
		}

		if (iterator.hasNext()) {
			return iterator.next().doFilter(channel, this);
		}

		return chain == null ? null : chain.doFilter(channel);
	}
}
