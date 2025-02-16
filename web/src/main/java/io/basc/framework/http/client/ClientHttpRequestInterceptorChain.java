package io.basc.framework.http.client;

import java.io.IOException;
import java.util.Iterator;

public class ClientHttpRequestInterceptorChain implements ClientHttpRequestExecutor {
	private Iterator<ClientHttpRequestInterceptor> iterator;
	private ClientHttpRequestExecutor nextChain;

	public ClientHttpRequestInterceptorChain(Iterator<ClientHttpRequestInterceptor> iterator) {
		this(iterator, null);
	}

	public ClientHttpRequestInterceptorChain(Iterator<ClientHttpRequestInterceptor> iterator,
			ClientHttpRequestExecutor nextChain) {
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	public ClientHttpResponse execute(ClientHttpRequest request) throws IOException {
		ClientHttpRequestInterceptor interceptor = getNextInterceptor(request);
		if (interceptor == null) {
			if (nextChain == null) {
				return request.execute();
			} else {
				return nextChain.execute(request);
			}
		}
		return interceptor.intercept(request, this);
	}

	protected ClientHttpRequestInterceptor getNextInterceptor(ClientHttpRequest request) {
		if (iterator == null) {
			return null;
		}

		while (iterator.hasNext()) {
			ClientHttpRequestInterceptor interceptor = iterator.next();
			if (interceptor != null) {
				return interceptor;
			}
		}
		return null;
	}
}
