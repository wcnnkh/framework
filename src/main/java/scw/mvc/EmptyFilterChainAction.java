package scw.mvc;

import scw.mvc.action.Action;

public final class EmptyFilterChainAction implements FilterChain {
	private final Action action;

	public EmptyFilterChainAction(Action action) {
		this.action = action;
	}

	public Object doFilter(Channel channel) throws Throwable {
		return action == null ? null : action.doAction(channel);
	}
}
