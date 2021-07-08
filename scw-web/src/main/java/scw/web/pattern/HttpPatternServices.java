package scw.web.pattern;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import scw.core.OrderComparator;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.web.ServerHttpRequest;

class HttpPatternServices<T> implements Comparator<T>, ServerHttpRequestAccept {
	private static Logger logger = LoggerFactory.getLogger(HttpPatternServices.class);
	private Set<T> services = new TreeSet<T>(this);

	/**
	 * 如果为0在TreeSet中会插入失败
	 */
	@Override
	public int compare(T o1, T o2) {
		if (o1 instanceof ServerHttpRequestAccept && o1 instanceof ServerHttpRequestAccept) {
			int v = OrderComparator.INSTANCE.compare(o1, o2);
			return v == 0 ? 1 : v;
		}

		if (o1 instanceof ServerHttpRequestAccept) {
			return 1;
		}

		if (o2 instanceof ServerHttpRequestAccept) {
			return -1;
		}

		int v = OrderComparator.INSTANCE.compare(o1, o2);
		return v == 0 ? 1 : v;
	}

	public boolean remove(T pattern) {
		return services.remove(pattern);
	}

	public boolean add(T service) {
		if (services.add(service)) {
			return true;
		}
		logger.error("add handler error: {}", service);
		return false;
	}

	public boolean contains(T pattern) {
		return services.contains(pattern);
	}

	public boolean isEmpty() {
		return services.isEmpty();
	}

	public T get(ServerHttpRequest request) {
		for (T service : services) {
			if (service instanceof ServerHttpRequestAccept) {
				if (((ServerHttpRequestAccept) service).accept(request)) {
					return service;
				}
			} else {
				// 因为services已经进行过排序了，ServerHttpRequestAccept一定在前面
				return service;
			}
		}
		return null;
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		return get(request) != null;
	}
}
