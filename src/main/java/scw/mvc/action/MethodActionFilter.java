package scw.mvc.action;

import scw.mvc.Channel;
import scw.mvc.FilterChain;

public abstract class MethodActionFilter extends ActionFilter {

	protected Object filter(Action action, Channel channel, FilterChain chain) throws Throwable {
		if (action instanceof MethodAction) {
			return filter((MethodAction) action, channel, chain);
		}
		return chain.doFilter(channel);
	}

	protected abstract Object filter(MethodAction action, Channel channel, FilterChain chain) throws Throwable;

}
