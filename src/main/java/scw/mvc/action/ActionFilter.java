package scw.mvc.action;

import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;

public abstract class ActionFilter implements Filter {
	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		Action<Channel> action = MVCUtils.getCurrentAction();
		if (action != null) {
			return filter(action, channel, chain);
		}
		return doFilter(channel, chain);
	}

	protected abstract Object filter(Action<Channel> action, Channel channel,
			FilterChain chain) throws Throwable;
}
