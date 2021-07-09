package scw.web.support;

import java.io.IOException;
import java.util.function.Supplier;

import scw.core.Assert;
import scw.http.HttpStatus;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.XUtils;
import scw.web.HttpService;
import scw.web.HttpServiceInterceptor;
import scw.web.HttpServiceInterceptorChain;
import scw.web.HttpServiceRegistry;
import scw.web.ServerHttpAsyncControl;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.ServerHttpResponseCompleteAsyncListener;
import scw.web.WebUtils;
import scw.web.cors.Cors;
import scw.web.cors.CorsRegistry;
import scw.web.cors.CorsUtils;

public abstract class AbstractHttpService implements HttpService {
	private static Logger logger = LoggerFactory.getLogger(HttpService.class);
	private final HttpServiceRegistry serviceRegistry;
	private CorsRegistry corsRegistry;
	private NotFoundServiceRegistry notFoundServiceRegistry;
	private Supplier<String> requestIdSupplier = XUtils::getUUID;

	public AbstractHttpService(HttpServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		String requestId = WebUtils.getRequestId(request);
		if(requestId == null) {
			requestId = requestIdSupplier.get();
			WebUtils.setRequestId(request, requestId);
		}
		CorsRegistry corsRegistry = getCorsRegistry();
		if (corsRegistry != null) {
			if (CorsUtils.isCorsRequest(request)) {
				Cors cors = corsRegistry.get(request);
				if (cors != null) {
					cors.write(request, response.getHeaders());
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(request.toString());
		}

		WebUtils.setLocalServerHttpRequest(request);
		Iterable<? extends HttpServiceInterceptor> interceptors = getHttpServiceInterceptors();
		HttpService service = serviceRegistry.get(request);
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
			} catch (Exception e) {
				WebUtils.setLocalServerHttpRequest(null);
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
