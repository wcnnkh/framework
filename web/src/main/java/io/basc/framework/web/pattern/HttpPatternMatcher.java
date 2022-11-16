package io.basc.framework.web.pattern;

import io.basc.framework.util.Registration;
import io.basc.framework.web.ServerHttpRequest;

public class HttpPatternMatcher<T> extends ServerHttpRequestMatcher<T> {
	private ServerHttpRequestMatcher<ServerHttpRequestAccept> excludeMatcher = new ServerHttpRequestMatcher<>();

	public Registration exclude(HttpPattern pattern) {
		return excludeMatcher.add(pattern);
	}

	public Registration exclude(ServerHttpRequestAccept requestAccept) {
		return excludeMatcher.add(requestAccept);
	}

	public Registration exclude(String pattern) {
		return excludeMatcher.add(new HttpPattern(pattern));
	}

	public Registration exclude(String pattern, String method) {
		return excludeMatcher.add(new HttpPattern(pattern, method));
	}

	@Override
	public boolean test(ServerHttpRequest request) {
		if (excludeMatcher.test(request)) {
			return false;
		}

		return super.test(request);
	}

	public T get(ServerHttpRequest request) {
		if (excludeMatcher.test(request)) {
			return null;
		}

		return super.get(request);
	}

	@Override
	public String toString() {
		return "excludeMatcher{" + excludeMatcher + "} matcher{" + super.toString() + "}";
	}
}
