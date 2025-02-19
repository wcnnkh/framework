package io.basc.framework.web.pattern;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

class HttpPatternServices<T> implements Comparator<HttpPatternService<T>>, ServerHttpRequestAccept {
	private static Logger logger = LogManager.getLogger(HttpPatternServices.class);
	private Set<HttpPatternService<T>> services = new TreeSet<HttpPatternService<T>>(this);

	@Override
	public int compare(HttpPatternService<T> o1, HttpPatternService<T> o2) {
		// 如果为0在TreeSet中会插入失败
		if (ObjectUtils.equals(o1, o2)) {
			return 0;
		}

		return o1.compareTo(o2);
	}

	public boolean remove(HttpPatternService<T> pattern) {
		return services.remove(pattern);
	}

	public boolean add(HttpPatternService<T> service) {
		if (services.add(service)) {
			return true;
		}

		HttpPatternService<T> exists = services.stream().filter((s) -> compare(s, service) == 0).findFirst().get();
		logger.error("add service [{}] exists [{}]", service, exists);
		return false;
	}

	public boolean contains(HttpPatternService<T> pattern) {
		return services.contains(pattern);
	}

	public boolean isEmpty() {
		return services.isEmpty();
	}

	public T get(ServerHttpRequest request) {
		for (HttpPatternService<T> service : services) {
			if (service.test(request)) {
				return service.getService();
			}
		}
		return null;
	}

	@Override
	public boolean test(ServerHttpRequest request) {
		return get(request) != null;
	}

	@Override
	public int hashCode() {
		return services.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof HttpPatternServices) {
			return CollectionUtils.unorderedEquals(services, ((HttpPatternServices<?>) obj).services);
		}
		return false;
	}

	@Override
	public String toString() {
		return services.toString();
	}
}
