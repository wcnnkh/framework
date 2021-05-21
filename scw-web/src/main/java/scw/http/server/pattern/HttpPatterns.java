package scw.http.server.pattern;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import scw.core.OrderComparator;
import scw.core.utils.CollectionUtils;
import scw.http.server.HttpService;
import scw.http.server.ServerHttpRequest;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class HttpPatterns<T> implements Comparator<T> {
	private static Logger logger = LoggerFactory.getLogger(HttpPatterns.class);
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

	public boolean remove(T service) {
		return services.remove(service);
	}

	public boolean add(T service) {
		if (services.add(service)) {
			return true;
		}
		logger.error("add handler error: {}", service);
		return false;
	}

	public boolean contains(HttpService service) {
		return services.contains(service);
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
			}
		}
		return CollectionUtils.first(services);
	}
}
