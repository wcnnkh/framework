package scw.http.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.http.HttpMethod;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.RestfulParameterMapAware;
import scw.net.Restful.RestfulMatchingResult;

public final class HttpServiceHandlerAccessor {
	private static Logger logger = LoggerUtils.getLogger(HttpServiceHandlerAccessor.class);

	private Map<String, EnumMap<HttpMethod, HttpServiceHandler>> handerMap = new HashMap<String, EnumMap<HttpMethod, HttpServiceHandler>>();
	private List<HttpServiceHandler> handlers = new ArrayList<HttpServiceHandler>();

	public void bind(String path, HttpMethod httpMethod, HttpServiceHandler handler) throws AlreadyExistsException {
		if (handler == null) {
			return;
		}

		EnumMap<HttpMethod, HttpServiceHandler> enumMap = handerMap.get(path);
		if (enumMap == null) {
			enumMap = new EnumMap<HttpMethod, HttpServiceHandler>(HttpMethod.class);
			handerMap.put(path, enumMap);
		}

		if (enumMap.containsKey(httpMethod)) {
			throw new AlreadyExistsException(httpMethod + " " + path);
		}

		enumMap.put(httpMethod, handler);
	}

	public void bind(HttpControllerDescriptor descriptor, HttpServiceHandler handler) throws AlreadyExistsException {
		bind(descriptor.getPath(), descriptor.getMethod(), handler);
	}

	public void bind(HttpServiceHandler handler) throws AlreadyExistsException {
		if (handler == null) {
			return;
		}

		if (handler instanceof HttpServiceHandlerControllerDesriptor) {
			HttpControllerDescriptor descriptor = ((HttpServiceHandlerControllerDesriptor) handler)
					.getControllerDescriptor();
			if (!descriptor.getRestful().isRestful()) {
				bind(descriptor, handler);
				return;
			}
		}

		handlers.add(handler);
	}

	public void bind(Collection<? extends HttpServiceHandler> handlers) {
		if (CollectionUtils.isEmpty(handlers)) {
			return;
		}

		for (HttpServiceHandler handler : handlers) {
			try {
				bind(handler);
			} catch (AlreadyExistsException e) {
				logger.error(e, "ignore bind");
			}
		}
	}

	public HttpServiceHandler get(ServerHttpRequest request) {
		EnumMap<HttpMethod, HttpServiceHandler> enumMap = handerMap.get(request.getPath());
		if (enumMap != null) {
			HttpServiceHandler handler = enumMap.get(request.getMethod());
			if (handler != null) {
				return handler;
			}
		}

		for (HttpServiceHandler handler : handlers) {
			if (handler instanceof HttpServiceHandlerControllerDesriptor) {
				HttpControllerDescriptor descriptor = ((HttpServiceHandlerControllerDesriptor) handler)
						.getControllerDescriptor();
				if (descriptor.getMethod() == null || descriptor.getMethod().equals(request.getMethod())) {
					RestfulMatchingResult result = descriptor.getRestful().matching(request.getPath());
					if (result.isSuccess()) {
						if (request instanceof RestfulParameterMapAware) {
							((RestfulParameterMapAware) request).setRestfulParameterMap(result.getParameterMap());
						}
						return handler;
					}
				}
			}

			if (handler instanceof HttpServiceHandlerAccept) {
				if (((HttpServiceHandlerAccept) handler).accept(request)) {
					return handler;
				}
			}
		}
		return null;
	}
}