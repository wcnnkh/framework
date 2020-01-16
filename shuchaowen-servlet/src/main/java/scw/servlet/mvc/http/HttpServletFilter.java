package scw.servlet.mvc.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.support.HttpFilter;

public abstract class HttpServletFilter extends HttpFilter {

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse, FilterChain chain)
			throws Throwable {
		if (httpRequest instanceof HttpServletRequest && httpResponse instanceof HttpServletResponse) {
			return doFilter(channel, (HttpServletRequest) httpRequest, (HttpServletResponse) httpResponse, chain);
		}

		return chain.doFilter(channel);
	}

	public abstract Object doFilter(HttpChannel channel, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain chain) throws Throwable;
}
