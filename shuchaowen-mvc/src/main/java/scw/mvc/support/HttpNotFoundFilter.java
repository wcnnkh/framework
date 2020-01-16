package scw.mvc.support;

import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.net.http.Method;

public final class HttpNotFoundFilter extends HttpFilter {
	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse, FilterChain chain) throws Throwable {
		if (Method.OPTIONS == httpRequest.getMethod()) {
			return chain.doFilter(channel);
		}

		channel.getLogger().warn("not found：{}", channel.toString());
		httpResponse.sendError(404, "not found action");
		return null;
	}

}
