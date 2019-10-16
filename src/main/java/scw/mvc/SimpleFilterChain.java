package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.action.Action;

public class SimpleFilterChain implements FilterChain {
	private Iterator<Filter> iterator;
	private Action<Channel> action;

	public SimpleFilterChain(Collection<Filter> filters) {
		this(filters, null);
	}

	public SimpleFilterChain(Collection<Filter> filters, Action<Channel> action) {
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
		this.action = action;
	}

	public Object doFilter(Channel channel) throws Throwable {
		if (iterator == null) {
			return action == null ? null : action.doAction(channel);
		}

		if (iterator.hasNext()) {
			return iterator.next().doFilter(channel, this);
		}
		return action == null ? null : action.doAction(channel);
	}
}
