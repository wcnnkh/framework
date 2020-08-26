package scw.http.server;

import java.io.IOException;
import java.util.Iterator;

public class HttpServiceInterceptorChain implements HttpService {
	private HttpServiceHandlerAccessor handlerAccessor;
	private Iterator<? extends HttpServiceInterceptor> iterator;

	public HttpServiceInterceptorChain(Iterator<? extends HttpServiceInterceptor> iterator,
			HttpServiceHandlerAccessor handlerAccessor) {
		this.iterator = iterator;
		this.handlerAccessor = handlerAccessor;
	}

	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		HttpServiceInterceptor interceptor = getNextHttpServiceInterceptor(request);
		if (interceptor == null) {
			if (handlerAccessor == null) {
				return;
			}

			HttpServiceHandler handler = handlerAccessor.get(request);
			if (handler != null) {
				handler.doHandle(request, response);
			}
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
