package scw.mvc.servlet;

import scw.mvc.Channel;
import scw.mvc.FilterChain;
import scw.mvc.action.Action;

public final class FilterChainAction implements Action<Channel> {
	private FilterChain filterChain;

	public FilterChainAction(FilterChain filterChain) {
		this.filterChain = filterChain;
	}

	public Object doAction(Channel channel) throws Throwable {
		return filterChain.doFilter(channel);
	}
}
