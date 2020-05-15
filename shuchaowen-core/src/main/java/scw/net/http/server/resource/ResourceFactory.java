package scw.net.http.server.resource;

import scw.net.http.server.ServerHttpRequest;

public interface ResourceFactory {
	Resource getResource(ServerHttpRequest request);
}
