package scw.mvc.action;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpMethodActionFilter extends MethodActionFilter {

	@Override
	protected Object filter(MethodAction action, Channel channel, ActionFilterChain chain) throws Throwable {
		if (channel instanceof HttpChannel) {
			return filter(action, (HttpChannel) channel, chain);
		}
		return chain.doFilter(action, channel);
	}

	protected abstract Object filter(MethodAction action, HttpChannel channel, ActionFilterChain chain)
			throws Throwable;

}
