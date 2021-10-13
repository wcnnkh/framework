package io.basc.framework.web.pattern;

import io.basc.framework.core.OrderComparator.OrderSourceProvider;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.web.ServerHttpRequest;

class ServerHttpRequestAcceptWrapper<T> implements ServerHttpRequestAccept, OrderSourceProvider {
	private final T service;
	private final HttpPattern pattern;

	public ServerHttpRequestAcceptWrapper(@Nullable HttpPattern pattern, T service) {
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
		if(pattern == null) {
			return service.toString();
		}
		return pattern.toString() + " -> " + service.toString();
	}

	@Override
	public int hashCode() {
		if (pattern == null) {
			return service.hashCode();
		}

		return pattern.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ServerHttpRequestAcceptWrapper) {
			if (pattern == null) {
				return ObjectUtils.nullSafeEquals(this.service, ((ServerHttpRequestAcceptWrapper<?>) obj).service);
			} else {
				return ObjectUtils.nullSafeEquals(this.pattern, ((ServerHttpRequestAcceptWrapper<?>) obj).pattern);
			}
		}
		return false;
	}
}
