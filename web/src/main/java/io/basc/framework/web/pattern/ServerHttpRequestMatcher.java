package io.basc.framework.web.pattern;

import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.util.AbstractHolder;
import io.basc.framework.util.Holder;
import io.basc.framework.web.ServerHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求匹配,线程安全的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public class ServerHttpRequestMatcher<T> implements ServerHttpRequestAccept {
	private Map<String, Map<String, HttpPatternServices<T>>> matcherMap = new HashMap<>();
	private HttpPatternServices<T> matchers = new HttpPatternServices<>();

	public Holder<T> add(String pattern, T service) throws AlreadyExistsException {
		return add(new HttpPattern(pattern), service);
	}

	private Holder<T> add(String pattern, String method, HttpPatternService<T> service) throws AlreadyExistsException {
		synchronized (matcherMap) {
			Map<String, HttpPatternServices<T>> map = matcherMap.get(pattern);
			if (map == null) {
				map = new HashMap<>();
				matcherMap.put(pattern, map);
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

		return new AbstractHolder<T>(() -> service.getService()) {

			@Override
			protected boolean releeaseInternal() {
				synchronized (matcherMap) {
					Map<String, HttpPatternServices<T>> map = matcherMap.get(pattern);
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

	public Holder<T> add(String pattern, String method, T service) throws AlreadyExistsException {
		return add(new HttpPattern(pattern, method), service);
	}

	public Holder<T> add(T service) throws AlreadyExistsException {
		return add(new HttpPatternService<T>(service));
	}

	private Holder<T> add(HttpPatternService<T> pattern) throws AlreadyExistsException {
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
			return add(new HttpPatternService<T>(service, httpPattern));
		} else {
			return add(httpPattern.getPath(), httpPattern.getMethod(), new HttpPatternService<T>(service, httpPattern));
		}
	}

	@Override
	public boolean test(ServerHttpRequest request) {
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
			service = matchers.get(request);
		}
		return service;
	}

	public final T get(ServerHttpRequest request, T defaultValue) {
		T value = get(request);
		return value == null ? defaultValue : value;
	}

	@Override
	public String toString() {
		return "matcherMap<" + matcherMap.toString() + "> matchers<" + matchers.toString() + ">";
	}
}
