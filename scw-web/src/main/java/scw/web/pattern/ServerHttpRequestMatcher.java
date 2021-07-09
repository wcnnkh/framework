package scw.web.pattern;

import java.util.HashMap;
import java.util.Map;

import scw.lang.AlreadyExistsException;
import scw.util.AbstractHolder;
import scw.util.Holder;
import scw.web.ServerHttpRequest;

/**
 * 请求匹配,线程安全的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class ServerHttpRequestMatcher<T> implements ServerHttpRequestAccept {
	private Map<String, Map<String, HttpPatternServices<T>>> matcherMap = new HashMap<>();
	private HttpPatternServices<ServerHttpRequestAcceptWrapper<T>> matchers = new HttpPatternServices<>();

	public Holder<T> add(String path, T service) throws AlreadyExistsException {
		return add(new HttpPattern(path), service);
	}

	public Holder<T> add(String path, String method, T service) throws AlreadyExistsException {
		synchronized (matcherMap) {
			Map<String, HttpPatternServices<T>> map = matcherMap.get(path);
			if (map == null) {
				map = new HashMap<>();
				matcherMap.put(path, map);
			}

			HttpPatternServices<T> httpPatterns = map.get(method);
			if (httpPatterns == null) {
				httpPatterns = new HttpPatternServices<>();
				map.put(method, httpPatterns);
			}

			if (!httpPatterns.add(service)) {
				throw new AlreadyExistsException(service.toString());
			}
		}

		return new AbstractHolder<T>(() -> service) {

			@Override
			protected boolean releeaseInternal() {
				synchronized (matcherMap) {
					Map<String, HttpPatternServices<T>> map = matcherMap.get(path);
					if (map == null) {
						return false;
					}

					HttpPatternServices<T> httpPatterns = map.get(method);
					if (httpPatterns == null) {
						return false;
					}

					return httpPatterns.remove(service);
				}
			}
		};
	}

	public Holder<T> add(T service) throws AlreadyExistsException {
		return add(new ServerHttpRequestAcceptWrapper<T>(null, service));
	}

	private Holder<T> add(ServerHttpRequestAcceptWrapper<T> pattern) throws AlreadyExistsException {
		synchronized (matchers) {
			if (!matchers.add(pattern)) {
				throw new AlreadyExistsException(pattern.toString());
			}
		}

		return new AbstractHolder<T>(() -> pattern.getService()) {

			@Override
			protected boolean releeaseInternal() {
				synchronized (matchers) {
					return matchers.remove(pattern);
				}
			}
		};
	}

	public Holder<T> add(HttpPattern httpPattern, T service) throws AlreadyExistsException {
		if (httpPattern.isPattern()) {
			return add(new ServerHttpRequestAcceptWrapper<T>(httpPattern, service));
		} else {
			return add(httpPattern.getPath(), httpPattern.getMethod(), service);
		}
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		return get(request) != null;
	}

	private T getMappingService(ServerHttpRequest request) {
		Map<String, HttpPatternServices<T>> map = matcherMap.get(request.getPath());
		if (map == null) {
			return null;
		}

		HttpPatternServices<T> services = map.get(request.getRawMethod());
		if (services == null) {
			return null;
		}

		return services.get(request);
	}

	public T get(ServerHttpRequest request) {
		T service = getMappingService(request);
		if (service == null) {
			ServerHttpRequestAcceptWrapper<T> pattern = matchers.get(request);
			if (pattern != null) {
				service = pattern.getService();
			}
		}
		return service;
	}

	public final T get(ServerHttpRequest request, T defaultValue) {
		T value = get(request);
		return value == null ? defaultValue : value;
	}
}
