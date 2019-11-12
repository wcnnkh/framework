package scw.mvc.action;

import scw.mvc.Channel;
import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;

public abstract class HttpActionFilter extends ActionFilter {

	protected Object filter(Action action, Channel channel,
			FilterChain chain) throws Throwable {
		if (channel instanceof HttpChannel && action instanceof HttpAction) {
			return filter((HttpAction) action, (HttpChannel) channel, chain);
		}
		return chain.doFilter(channel);
	}

	protected abstract Object filter(HttpAction action, HttpChannel channel,
			FilterChain chain) throws Throwable;

}
