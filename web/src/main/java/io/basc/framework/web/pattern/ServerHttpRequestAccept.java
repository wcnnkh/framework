package io.basc.framework.web.pattern;

import java.util.function.Predicate;

import io.basc.framework.web.ServerHttpRequest;

public interface ServerHttpRequestAccept extends Predicate<ServerHttpRequest> {
	@Override
	boolean test(ServerHttpRequest request);
}
