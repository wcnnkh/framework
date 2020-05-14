package scw.mvc.resource;

import scw.net.http.server.ServerHttpRequest;

public interface ResourceFactory {
	Resource getResource(ServerHttpRequest request);
}
