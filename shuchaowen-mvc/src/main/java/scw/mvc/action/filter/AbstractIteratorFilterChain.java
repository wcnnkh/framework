package scw.mvc.action.filter;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public abstract class AbstractIteratorFilterChain implements FilterChain {
	private final FilterChain chain;

	public AbstractIteratorFilterChain(FilterChain chain) {
		this.chain = chain;
	}

	public final Object doFilter(Channel channel, Action action)
			throws Throwable {
		Filter filter = getNextFilter(channel, action);
		if (filter == null) {
			if (chain == null) {
				return action.doAction(channel);
			} else {
				return chain.doFilter(channel, action);
			}
		}
		return filter.doFilter(channel, action, this);
	}

	protected abstract Filter getNextFilter(Channel channel, Action action)
			throws Throwable;
}
