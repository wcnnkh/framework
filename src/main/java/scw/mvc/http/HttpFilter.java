package scw.mvc.http;

import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;

public abstract class HttpFilter implements Filter {
	@SuppressWarnings("rawtypes")
	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		if (channel instanceof HttpChannel) {
			return doFilter(channel, (HttpRequest) ((HttpChannel) channel).getRequest(),
					(HttpResponse) ((HttpChannel) channel).getResponse(), chain);
		}
		return chain.doFilter(channel);
	}

	public abstract Object doFilter(Channel channel, HttpRequest httpRequest, HttpResponse httpResponse,
			FilterChain chain) throws Throwable;
}
