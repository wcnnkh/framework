package scw.mvc.action.filter;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public abstract class AbstractIteratorActionFilterChain implements ActionFilterChain {
	private final ActionFilterChain chain;

	public AbstractIteratorActionFilterChain(ActionFilterChain chain) {
		this.chain = chain;
	}

	public final Object doFilter(Channel channel, Action action)
			throws Throwable {
		ActionFilter filter = getNextFilter(channel, action);
		if (filter == null) {
			if (chain == null) {
				return action.doAction(channel);
			} else {
				return chain.doFilter(channel, action);
			}
		}
		return filter.doFilter(channel, action, this);
	}

	protected abstract ActionFilter getNextFilter(Channel channel, Action action)
			throws Throwable;
}
