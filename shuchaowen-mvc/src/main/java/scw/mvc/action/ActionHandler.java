package scw.mvc.action;

import java.util.Collection;

import scw.mvc.Channel;
import scw.mvc.action.exception.ActionExceptionHandlerChain;
import scw.mvc.action.filter.Filter;
import scw.mvc.action.filter.FilterChain;
import scw.mvc.action.filter.IteratorFilterChain;
import scw.mvc.context.ContextManager;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;
import scw.mvc.output.Output;

public class ActionHandler implements Handler {
	private final ActionFactory actionFactory;
	private final Collection<Filter> filters;
	private Output output;
	private final ActionExceptionHandlerChain actionExceptionHandlerChain;

	public ActionHandler(ActionFactory actionFactory,
			Collection<Filter> filters, Output output,
			ActionExceptionHandlerChain actionExceptionHandlerChain) {
		this.actionFactory = actionFactory;
		this.filters = filters;
		this.output = output;
		this.actionExceptionHandlerChain = actionExceptionHandlerChain;
	}

	public void doHandler(Channel channel, HandlerChain chain) throws Throwable {
		Action action = actionFactory.getAction(channel);
		if (action == null) {
			chain.doHandler(channel);
			return;
		}

		output.write(channel, doAction(channel, action, chain));
	}

	protected Object doAction(Channel channel, Action action, HandlerChain chain) {
		FilterChain filterChain = new IteratorFilterChain(filters, action.getFilterChain());
		try {
			return ContextManager.doFilter(channel, action, filterChain);
		} catch (Throwable e) {
			channel.getLogger().error(e, channel.toString());
			return actionExceptionHandlerChain.doHandler(channel, action, e);
		}
	}
}
