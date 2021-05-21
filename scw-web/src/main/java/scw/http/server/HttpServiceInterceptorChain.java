package scw.http.server;

import java.io.IOException;
import java.util.Iterator;

import scw.http.HttpStatus;
import scw.http.server.cors.CorsUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class HttpServiceInterceptorChain implements HttpService {
	private static Logger logger = LoggerFactory.getLogger(HttpService.class);
	private Iterator<? extends HttpServiceInterceptor> iterator;
	private HttpService service;

	public HttpServiceInterceptorChain(Iterator<? extends HttpServiceInterceptor> iterator, HttpService service) {
		this.iterator = iterator;
		this.service = service;
	}

	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		HttpServiceInterceptor interceptor = getNextHttpServiceInterceptor(request);
		if (interceptor == null) {
			if (service == null) {
				notfound(request, response);
				return;
			}
			service.service(request, response);
			return;
		}
		interceptor.intercept(request, response, this);
	}

	protected void notfound(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		if (CorsUtils.isPreFlightRequest(request)) {
			return;
		}

		response.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
		logger.error("Not found {}", request.toString());
	}

	private HttpServiceInterceptor getNextHttpServiceInterceptor(ServerHttpRequest request) {
		if (hasNext()) {
			return iterator.next();
		}
		return null;
	}

	private boolean hasNext() {
		return iterator != null && iterator.hasNext();
	}
}
