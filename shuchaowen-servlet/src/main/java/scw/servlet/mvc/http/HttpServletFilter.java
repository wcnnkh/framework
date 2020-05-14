package scw.servlet.mvc.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;

public abstract class HttpServletFilter implements ActionFilter {
	public Object doFilter(Channel channel, Action action, ActionFilterChain chain) throws Throwable {
		if (channel instanceof HttpServletChannel) {
			HttpServletChannel httpServletChannel = (HttpServletChannel) channel;
			return doHttpServletFilter(httpServletChannel, httpServletChannel.getHttpServletRequest(),
					httpServletChannel.getHttpServletResponse(), action, chain);
		}
		return chain.doFilter(channel, action);
	}

	protected abstract Object doHttpServletFilter(HttpServletChannel channel, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Action action, ActionFilterChain chain) throws Throwable;
}
