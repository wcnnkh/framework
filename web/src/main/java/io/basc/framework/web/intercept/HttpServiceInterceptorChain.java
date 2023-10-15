package io.basc.framework.web.intercept;

import java.io.IOException;
import java.util.Iterator;

import io.basc.framework.web.HttpService;
import io.basc.framework.web.HttpServiceInterceptor;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;

public class HttpServiceInterceptorChain implements HttpService {
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
				return;
			}
			service.service(request, response);
			return;
		}
		interceptor.intercept(request, response, this);
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
