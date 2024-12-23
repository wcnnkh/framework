package io.basc.framework.web.pattern;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.comparator.OrderComparator;
import io.basc.framework.util.comparator.OrderComparator.OrderSourceProvider;
import io.basc.framework.web.ServerHttpRequest;

class HttpPatternService<T> implements ServerHttpRequestAccept, OrderSourceProvider, Comparable<HttpPatternService<T>> {
	private final T service;
	private HttpPattern pattern;

	public HttpPatternService(T service) {
		this.service = service;
		if (service instanceof HttpPattern) {
			this.pattern = (HttpPattern) service;
		}
	}

	public HttpPatternService(T service, @Nullable HttpPattern pattern) {
		this.pattern = pattern;
		this.service = service;
	}

	@Override
	public boolean test(ServerHttpRequest request) {
		if (pattern != null && !pattern.test(request)) {
			return false;
		}

		if (service instanceof ServerHttpRequestAccept) {
			return ((ServerHttpRequestAccept) service).test(request);
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
		if (pattern == null) {
			return service.toString();
		}
		return pattern.toString() + " -> " + service.toString();
	}

	@Override
	public int hashCode() {
		if (pattern == null) {
			return service.hashCode();
		}

		return pattern.hashCode() + service.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof HttpPatternService) {
			if (pattern == null) {
				return ObjectUtils.equals(service, ((HttpPatternService<?>) obj).service);
			} else {
				return ObjectUtils.equals(this.pattern, ((HttpPatternService<?>) obj).pattern);
			}
		}
		return false;
	}

	@Override
	public int compareTo(HttpPatternService<T> o) {
		if (pattern == null && o.pattern == null) {
			if (service instanceof ServerHttpRequestAccept && o.service instanceof ServerHttpRequestAccept) {
				int v = OrderComparator.INSTANCE.compare(service, o.service);
				// 如果order相同那么就按添加顺序来
				return v == 0 ? 1 : v;
			}

			if (service instanceof ServerHttpRequestAccept) {
				return -1;
			}

			if (o.service instanceof ServerHttpRequestAccept) {
				return 1;
			}

			// 如果order相同那么就按添加顺序来
			int v = OrderComparator.INSTANCE.compare(service, o.service);
			return v == 0 ? 1 : v;
		}

		if (pattern == null) {
			return 1;
		}

		if (o.pattern == null) {
			return -1;
		}

		return pattern.compareTo(o.pattern);
	}
}
