package scw.mvc.service;

import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.manager.ActionLookupManager;
import scw.mvc.http.HttpChannel;
import scw.net.http.HttpMethod;

@Configuration(order = Integer.MIN_VALUE)
public class ActionServiceFilter implements Filter {
	private ActionLookupManager actionLookupManager;

	public ActionServiceFilter(ActionLookupManager actionLookupManager) {
		this.actionLookupManager = actionLookupManager;
	}

	public Object doFilter(Channel channel, FilterChain chain)
			throws Throwable {
		Action action = actionLookupManager.lookup(channel);
		if (action == null) {
			channel.getLogger().warn("not found：{}", channel.toString());
			return notfound(channel, chain);
		}

		return doAction(channel, action, chain);
	}

	protected Object doAction(Channel channel, Action action, FilterChain chain)
			throws Throwable {
		return ContextManager.doFilter(channel, action,
				action.getActionFilterChain());
	}

	protected Object notfound(Channel channel, FilterChain chain)
			throws Throwable {
		if (channel instanceof HttpChannel) {
			return notfoundHttpAction((HttpChannel) channel, chain);
		}
		return chain.doFilter(channel);
	}

	protected Object notfoundHttpAction(HttpChannel channel, FilterChain chain)
			throws Throwable {
		if (HttpMethod.OPTIONS == channel.getRequest().getMethod()) {
			return chain.doFilter(channel);
		}
		channel.getResponse().sendError(404, "not found handler");
		return null;
	}
}
