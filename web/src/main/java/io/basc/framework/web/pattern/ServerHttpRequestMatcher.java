package io.basc.framework.web.pattern;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.lang.AlreadyExistsException;
import io.basc.framework.util.register.StandardRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.web.ServerHttpRequest;

public class ServerHttpRequestMatcher<T> implements ServerHttpRequestAccept {
	private Map<String, Map<String, HttpPatternServices<T>>> matcherMap = new HashMap<>();
	private HttpPatternServices<T> matchers = new HttpPatternServices<>();

	public Registration add(String pattern, T service) throws AlreadyExistsException {
		return add(new HttpPattern(pattern), service);
	}

	private Registration add(String pattern, String method, HttpPatternService<T> service)
			throws AlreadyExistsException {
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

		return StandardRegistration.of(() -> {
			synchronized (matcherMap) {
				Map<String, HttpPatternServices<T>> map = matcherMap.get(pattern);
				if (map == null) {
					return;
				}

				HttpPatternServices<T> httpPatterns = map.get(method);
				if (httpPatterns == null) {
					return;
				}

				httpPatterns.remove(service);
			}
		});
	}

	public Registration add(String pattern, String method, T service) throws AlreadyExistsException {
		return add(new HttpPattern(pattern, method), service);
	}

	public Registration add(T service) throws AlreadyExistsException {
		return add(new HttpPatternService<T>(service));
	}

	private Registration add(HttpPatternService<T> pattern) throws AlreadyExistsException {
		synchronized (matchers) {
			if (!matchers.add(pattern)) {
				throw new AlreadyExistsException(pattern.toString());
			}
		}

		return StandardRegistration.of(() -> {
			synchronized (matchers) {
				matchers.remove(pattern);
			}
		});
	}

	public Registration add(HttpPattern httpPattern, T service) throws AlreadyExistsException {
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
