package scw.mvc.http.filter;

import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

public final class NotFoundFilter extends HttpFilter {
	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse, FilterChain chain) throws Throwable {
		channel.getLogger().warn("not found：{}", channel.toString());
		httpResponse.sendError(404, "not found action");
		return null;
	}

}
