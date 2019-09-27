package scw.mvc.action;

import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpActionFilter implements ActionFilter {

	public Object filter(Action<Channel> action, Channel channel, ActionFilterChain chain) throws Throwable {
		if (channel instanceof HttpChannel && action instanceof HttpAction) {
			return filter((HttpAction) action, (HttpChannel) channel, chain);
		}
		return chain.doFilter(action, channel);
	}

	public abstract Object filter(HttpAction action, HttpChannel channel, ActionFilterChain chain) throws Throwable;

}
