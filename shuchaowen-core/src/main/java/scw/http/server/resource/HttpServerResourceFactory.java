package scw.http.server.resource;

import scw.http.server.ServerHttpRequest;

public interface HttpServerResourceFactory {
	HttpServerResource getResource(ServerHttpRequest request);
}
