package scw.mvc.support;

import scw.core.exception.NotSupportException;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;

public abstract class ActionFilter implements Filter {
	public final Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		Action action = MVCUtils.getCurrentAction();
		if (action == null) {
			return notFoundAction(channel, chain);
		}

		return doFilter(action, channel, chain);
	}

	protected Object notFoundAction(Channel channel, FilterChain chain) throws Throwable {
		throw new NotSupportException(channel.toString());
	}

	protected abstract Object doFilter(Action action, Channel channel, FilterChain chain) throws Throwable;
}
