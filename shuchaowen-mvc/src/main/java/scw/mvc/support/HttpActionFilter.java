package scw.mvc.support;

import scw.lang.NotSupportException;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.support.action.HttpAction;

public abstract class HttpActionFilter extends ActionFilter {

	protected final Object notHttp(Action action, Channel channel, FilterChain chain) throws Throwable {
		throw new NotSupportException(action.toString());
	}

	public final Object doFilter(Action action, Channel channel, FilterChain chain) throws Throwable {
		if (action instanceof HttpAction && channel instanceof HttpChannel) {
			return doFilter((HttpAction) action, (HttpChannel) channel, chain);
		}

		return notHttp(action, channel, chain);
	}

	protected abstract Object doFilter(HttpAction httpAction, HttpChannel httpChannel, FilterChain chain)
			throws Throwable;

}
