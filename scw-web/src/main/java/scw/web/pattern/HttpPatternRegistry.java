package scw.web.pattern;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.core.OrderComparator.OrderSourceProvider;
import scw.http.HttpMethod;
import scw.lang.AlreadyExistsException;
import scw.web.ServerHttpRequest;

public class HttpPatternRegistry<T> {
	private Map<String, EnumMap<HttpMethod, HttpPatterns<T>>> serviceMap = new HashMap<String, EnumMap<HttpMethod, HttpPatterns<T>>>();
	private HttpPatterns<InternalHttpService<T>> services = new HttpPatterns<InternalHttpService<T>>();

	public HttpPatternRegsitration<T> register(T service) throws AlreadyExistsException {
		synchronized (services) {
			InternalHttpService<T> config = new InternalHttpService<T>(null, service);
			if (!services.add(config)) {
				throw new AlreadyExistsException(service.toString());
			}

			return new HttpPatternRegsitration<T>(null, service) {

				@Override
				public boolean unregister() {
					synchronized (services) {
						return services.remove(config);
					}
				}
			};
		}
	}

	public HttpPatternRegsitration<T> register(HttpPattern pattern, T service) throws AlreadyExistsException {
		if (pattern.isPattern()) {
			synchronized (services) {
				InternalHttpService<T> config = new InternalHttpService<T>(pattern, service);
				if (!services.add(config)) {
					throw new AlreadyExistsException(service.toString());
				}

				return new HttpPatternRegsitration<T>(pattern, service) {

					@Override
					public boolean unregister() {
						synchronized (services) {
							return services.remove(config);
						}
					}
				};
			}
		} else {
			synchronized (serviceMap) {
				EnumMap<HttpMethod, HttpPatterns<T>> map = serviceMap.get(pattern.getPath());
				if (map == null) {
					map = new EnumMap<HttpMethod, HttpPatterns<T>>(HttpMethod.class);
					serviceMap.put(pattern.getPath(), map);
				}

				HttpPatterns<T> services = map.get(pattern.getMethod());
				if (services == null) {
					services = new HttpPatterns<T>();
					map.put(pattern.getMethod(), services);
				}

				if (!services.add(service)) {
					throw new AlreadyExistsException(service.toString());
				}

				return new HttpPatternRegsitration<T>(pattern, service) {

					@Override
					public boolean unregister() {
						synchronized (serviceMap) {
							EnumMap<HttpMethod, HttpPatterns<T>> map = serviceMap.get(pattern.getPath());
							if (map == null) {
								return false;
							}

							HttpPatterns<T> services = map.get(pattern.getMethod());
							if (services == null) {
								return false;
							}
							return services.remove(service);
						}
					}
				};
			}
		}
	}

	private static class InternalHttpService<T> implements ServerHttpRequestAccept, OrderSourceProvider {
		private final T service;
		private final HttpPattern pattern;

		public InternalHttpService(HttpPattern pattern, T service) {
			this.pattern = pattern;
			this.service = service;
		}
		
		@Override
		public boolean accept(ServerHttpRequest request) {
			if(pattern != null && !pattern.accept(request)) {
				return false;
			}
			
			if(service instanceof ServerHttpRequestAccept) {
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
	}

	private T getMappingService(ServerHttpRequest request) {
		EnumMap<HttpMethod, HttpPatterns<T>> map = serviceMap.get(request.getPath());
		if (map == null) {
			return null;
		}

		HttpPatterns<T> services = map.get(request.getMethod());
		if (services == null) {
			return null;
		}

		return services.get(request);
	}

	public T get(ServerHttpRequest request) {
		T service = getMappingService(request);
		if (service == null) {
			InternalHttpService<T> internalHttpService = services.get(request);
			if (internalHttpService != null) {
				return internalHttpService.getService();
			}
		}
		return service;
	}

	public T get(ServerHttpRequest request, T defaultValue) {
		T value = get(request);
		return value == null ? defaultValue : value;
	}
}
