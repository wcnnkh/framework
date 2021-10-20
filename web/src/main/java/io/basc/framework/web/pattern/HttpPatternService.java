package io.basc.framework.web.pattern;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.core.OrderComparator.OrderSourceProvider;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.web.ServerHttpRequest;

 class HttpPatternService<T> implements ServerHttpRequestAccept, OrderSourceProvider, Comparable<HttpPatternService<T>> {
	private final T service;
	private final HttpPattern pattern;

	public HttpPatternService(@Nullable HttpPattern pattern, T service) {
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

		return pattern.hashCode() + service.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof HttpPatternService) {
			int order = OrderComparator.INSTANCE.compare(service, ((HttpPatternService<?>) obj).service);
			if(pattern == null && ((HttpPatternService<?>) obj).pattern == null) {
				return order == 0;
			}else {
				return ObjectUtils.nullSafeEquals(this.pattern, ((HttpPatternService<?>) obj).pattern) && order == 0;
			}
		}
		return false;
	}

	@Override
	public int compareTo(HttpPatternService<T> o) {
		if(pattern == null && o.pattern == null) {
			if(service instanceof ServerHttpRequestAccept && o.service instanceof ServerHttpRequestAccept) {
				return OrderComparator.INSTANCE.compare(service, o.service);
			}
			
			if(service instanceof ServerHttpRequestAccept) {
				return -1;
			}
			
			if(o.service instanceof ServerHttpRequestAccept) {
				return 1;
			}
			
			//无法比较，返回0
			return 0;
		}
		
		if(pattern == null) {
			return 1;
		}
		
		if(o.pattern == null) {
			return -1;
		}
		
		return pattern.compareTo(o.pattern);
	}
}
