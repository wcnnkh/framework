package io.basc.framework.http;

import io.basc.framework.lang.Nullable;
import io.basc.framework.net.Request;
import io.basc.framework.net.pattern.PathPattern;

public interface HttpPattern extends PathPattern {
	@Nullable
	String getMethod();

	default boolean test(HttpRequest request, String method) {
		return method.equalsIgnoreCase(request.getRawMethod());
	}

	@Override
	default boolean test(Request request) {
		String method = getMethod();
		if (method != null) {
			if (request instanceof HttpRequest) {
				if (!test((HttpRequest) request, method)) {
					return false;
				}
			}
			return false;
		}
		return PathPattern.super.test(request);
	}
}
