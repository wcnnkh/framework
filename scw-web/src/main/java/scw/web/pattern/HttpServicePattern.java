package scw.web.pattern;

import scw.core.OrderComparator.OrderSourceProvider;
import scw.web.ServerHttpRequest;

class HttpServicePattern<T> implements ServerHttpRequestAccept, OrderSourceProvider {
	private final T service;
	private final HttpPattern pattern;

	public HttpServicePattern(HttpPattern pattern, T service) {
		this.pattern = pattern;
		this.service = service;
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		if (pattern != null && !pattern.accept(request)) {
			return false;
		}

		if (service instanceof ServerHttpRequestAccept) {
			return ((ServerHttpRequestAccept) service).accept(request);
		}
		return true;
	}

	public T getService() {
		return service;
	}

	@Override
	public Object getOrderSource(Object obj) {
		return service;
	}

	@Override
	public String toString() {
		return service.toString();
	}
}
