package io.basc.framework.web.resource;

import io.basc.framework.http.server.ServerHttpRequest;

@FunctionalInterface
public interface StaticResourceResolver {
	String resolve(ServerHttpRequest request);
}
