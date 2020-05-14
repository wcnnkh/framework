package scw.net.http.server.filter;

import scw.net.http.server.HttpServer;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public interface HttpServerFilter {
	Object doFilter(ServerHttpRequest request, ServerHttpResponse response, HttpServer httpServer)
			throws Throwable;
}
