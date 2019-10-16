package scw.mvc.action;

import scw.mvc.Channel;

public abstract class MethodActionFilter implements ActionFilter {

	public Object filter(Action<Channel> action, Channel channel, ActionFilterChain chain) throws Throwable {
		if (action instanceof MethodAction) {
			return filter((MethodAction) action, channel, chain);
		}
		return chain.doFilter(action, channel);
	}

	protected abstract Object filter(MethodAction action, Channel channel, ActionFilterChain chain) throws Throwable;

}
