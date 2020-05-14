package scw.net.http.server.handler;

import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public interface HttpServerHandler {
	boolean isSupports(ServerHttpRequest request);

	Object handler(ServerHttpRequest request, ServerHttpResponse response) throws Throwable;
}
