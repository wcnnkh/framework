package io.basc.framework.web.resource;

import io.basc.framework.web.ServerHttpRequest;

@FunctionalInterface
public interface StaticResourceResolver {
	String resolve(ServerHttpRequest request);
}
