package io.basc.framework.web.pattern;

import io.basc.framework.util.Holder;
import io.basc.framework.web.ServerHttpRequest;

public class HttpPatternMatcher<T> extends ServerHttpRequestMatcher<T> {
	private ServerHttpRequestMatcher<ServerHttpRequestAccept> excludeMatcher = new ServerHttpRequestMatcher<>();

	public Holder<ServerHttpRequestAccept> exclude(HttpPattern pattern) {
		return excludeMatcher.add(pattern);
	}

	public Holder<ServerHttpRequestAccept> exclude(ServerHttpRequestAccept requestAccept) {
		return excludeMatcher.add(requestAccept);
	}

	public Holder<ServerHttpRequestAccept> exclude(String pattern) {
		return excludeMatcher.add(new HttpPattern(pattern));
	}

	public Holder<ServerHttpRequestAccept> exclude(String pattern, String method) {
		return excludeMatcher.add(new HttpPattern(pattern, method));
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		if (excludeMatcher.accept(request)) {
			return false;
		}

		return super.accept(request);
	}

	public T get(ServerHttpRequest request) {
		if (excludeMatcher.accept(request)) {
			return null;
		}

		return super.get(request);
	}
	
	@Override
	public String toString() {
		return "excludeMatcher{" + excludeMatcher + "} matcher{" + super.toString() + "}";
	}
}
