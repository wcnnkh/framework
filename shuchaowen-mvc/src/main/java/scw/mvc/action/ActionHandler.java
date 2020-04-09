package scw.mvc.action;

import java.util.Collection;

import scw.mvc.Channel;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.action.filter.IteratorActionFilterChain;
import scw.mvc.context.ContextManager;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;

public class ActionHandler implements Handler {
	private final ActionFactory actionFactory;
	private final Collection<ActionFilter> filters;

	public ActionHandler(ActionFactory actionFactory,
			Collection<ActionFilter> filters) {
		this.actionFactory = actionFactory;
		this.filters = filters;
	}

	public Object doHandler(Channel channel, HandlerChain chain)
			throws Throwable {
		Action action = actionFactory.getAction(channel);
		if (action == null) {
			return chain.doHandler(channel);
		}

		return doAction(channel, action, chain);
	}

	protected Object doAction(Channel channel, Action action, HandlerChain chain)
			throws Throwable {
		ActionFilterChain filterChain = new IteratorActionFilterChain(filters,
				action.getActionFilterChain());
		return ContextManager.doFilter(channel, action, filterChain);
	}
}
