package scw.mvc.handler;

import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.manager.ActionLookupManager;
import scw.mvc.context.ContextManager;
import scw.mvc.http.HttpChannel;
import scw.net.http.HttpMethod;

@Configuration(order = Integer.MIN_VALUE)
public class ActionHandler implements Handler {
	private ActionLookupManager actionLookupManager;

	public ActionHandler(ActionLookupManager actionLookupManager) {
		this.actionLookupManager = actionLookupManager;
	}

	public Object doHandler(Channel channel, HandlerChain chain)
			throws Throwable {
		Action action = actionLookupManager.lookup(channel);
		if (action == null) {
			channel.getLogger().warn("not found：{}", channel.toString());
			return notfound(channel, chain);
		}

		return doAction(channel, action, chain);
	}

	protected Object doAction(Channel channel, Action action, HandlerChain chain)
			throws Throwable {
		return ContextManager.doFilter(channel, action,
				action.getActionFilterChain());
	}

	protected Object notfound(Channel channel, HandlerChain chain)
			throws Throwable {
		if (channel instanceof HttpChannel) {
			return notfoundHttpAction((HttpChannel) channel, chain);
		}
		return chain.doHandler(channel);
	}

	protected Object notfoundHttpAction(HttpChannel channel, HandlerChain chain)
			throws Throwable {
		if (HttpMethod.OPTIONS == channel.getRequest().getMethod()) {
			return chain.doHandler(channel);
		}
		channel.getResponse().sendError(404, "not found handler");
		return null;
	}
}
