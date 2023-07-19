package io.basc.framework.web.support;

import java.io.IOException;
import java.util.function.Supplier;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.HttpServiceInterceptor;
import io.basc.framework.web.HttpServiceInterceptorChain;
import io.basc.framework.web.HttpServiceRegistry;
import io.basc.framework.web.ServerHttpAsyncControl;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.ServerHttpResponseCompleteAsyncListener;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.cors.Cors;
import io.basc.framework.web.cors.CorsRegistry;
import io.basc.framework.web.cors.CorsUtils;
import io.basc.framework.web.resource.StaticResourceHttpService;

public abstract class AbstractHttpService implements HttpService, Configurable {
	private static Logger logger = LoggerFactory.getLogger(HttpService.class);
	private final ConfigurableServices<NotFoundServiceRegistry> notFoundServiceRegistrys = new ConfigurableServices<>(
			NotFoundServiceRegistry.class);
	private final HttpServiceRegistry httpServiceRegistry = new HttpServiceRegistry();
	private final ConfigurableServices<CorsRegistry> corsRegistrys = new ConfigurableServices<>(CorsRegistry.class);
	private final StaticResourceHttpService staticResourceHttpService = new StaticResourceHttpService();
	private Supplier<String> requestIdSupplier = XUtils::getUUID;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		notFoundServiceRegistrys.configure(serviceLoaderFactory);
		corsRegistrys.configure(serviceLoaderFactory);
		staticResourceHttpService.configure(serviceLoaderFactory);
		serviceLoaderFactory.getServiceLoader(HttpService.class).getServices().forEach(httpServiceRegistry::add);
	}

	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		String messageId = WebUtils.getMessageId(request, response);
		CorsRegistry corsRegistry = getCorsRegistry();
		if (corsRegistry != null) {
			if (CorsUtils.isCorsRequest(request)) {
				Cors cors = corsRegistry.process(request);
				if (cors != null) {
					cors.write(request, response.getHeaders());
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Request[{}] {}", messageId, request.toString());
		}

		WebUtils.setLocalServerHttpRequest(request);
		Iterable<? extends HttpServiceInterceptor> interceptors = getHttpServiceInterceptors();
		HttpService service = httpServiceRegistry.get(request);
		if (service == null) {
			// not found
			NotFoundServiceRegistry notFoundServiceRegistry = getNotFoundServiceRegistry();
			if (notFoundServiceRegistry != null) {
				service = notFoundServiceRegistry.get(request);
			}
		}

		if (service == null) {
			service = (req, resp) -> notfound(req, resp);
		}

		HttpService serviceToUse = new HttpServiceInterceptorChain(
				interceptors == null ? null : interceptors.iterator(), service);
		try {
			serviceToUse.service(request, response);
		} finally {
			try {
				if (!response.isCommitted()) {
					if (request.isSupportAsyncControl()) {
						ServerHttpAsyncControl serverHttpAsyncControl = request.getAsyncControl(response);
						if (serverHttpAsyncControl.isStarted()) {
							serverHttpAsyncControl.addListener(new ServerHttpResponseCompleteAsyncListener(response));
							return;
						}
					}
				}
				response.close();
			} finally {
				WebUtils.setLocalServerHttpRequest(null);
				if (logger.isDebugEnabled()) {
					logger.debug("End of request[{}]", messageId);
				}
			}
		}
	}

	public void notfound(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		if (CorsUtils.isPreFlightRequest(request)) {
			return;
		}

		response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
		logger.error("Not found {}", request.toString());
	}

	public NotFoundServiceRegistry getNotFoundServiceRegistry() {
		return notFoundServiceRegistry;
	}

	public void setNotFoundServiceRegistry(NotFoundServiceRegistry notFoundServiceRegistry) {
		this.notFoundServiceRegistry = notFoundServiceRegistry;
	}

	public HttpServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public Supplier<String> getRequestIdSupplier() {
		return requestIdSupplier;
	}

	public void setRequestIdSupplier(Supplier<String> requestIdSupplier) {
		Assert.requiredArgument(requestIdSupplier != null, "requestIdSupplier");
		this.requestIdSupplier = requestIdSupplier;
	}

	public CorsRegistry getCorsRegistry() {
		return corsRegistry;
	}

	public void setCorsRegistry(CorsRegistry corsRegistry) {
		this.corsRegistry = corsRegistry;
	}

	public abstract Iterable<? extends HttpServiceInterceptor> getHttpServiceInterceptors();
}
