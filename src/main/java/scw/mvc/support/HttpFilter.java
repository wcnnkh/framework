package scw.mvc.support;

import scw.lang.NotSupportException;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

public abstract class HttpFilter implements Filter {

	public final Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		if (channel instanceof HttpChannel) {
			return doFilter((HttpChannel) channel, (HttpRequest) ((HttpChannel) channel).getRequest(),
					(HttpResponse) ((HttpChannel) channel).getResponse(), chain);
		}
		return notHttp(channel, chain);
	}

	protected Object notHttp(Channel channel, FilterChain chain) throws Throwable {
		throw new NotSupportException(channel.toString());
	}

	protected abstract Object doFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse,
			FilterChain chain) throws Throwable;
}
