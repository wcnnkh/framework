package scw.mvc.action;

import scw.mvc.Channel;
import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;

public abstract class HttpMethodActionFilter extends MethodActionFilter {

	@Override
	protected Object filter(MethodAction action, Channel channel, FilterChain chain) throws Throwable {
		if (channel instanceof HttpChannel) {
			return filter(action, (HttpChannel) channel, chain);
		}
		return chain.doFilter(channel);
	}

	protected abstract Object filter(MethodAction action, HttpChannel channel, FilterChain chain)
			throws Throwable;

}
