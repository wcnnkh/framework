package io.basc.framework.web;

import java.io.IOException;

public interface HttpServiceInterceptor extends WebServiceInterceptor {
	@Override
	default void intercept(ServerRequest serverRequest, ServerResponse serverResponse, WebService chain)
			throws IOException, WebException {
		if (serverRequest instanceof ServerHttpRequest && serverResponse instanceof ServerHttpResponse) {
			intercept((ServerHttpRequest) serverRequest, (ServerHttpResponse) serverResponse, chain);
		}
	}

	void intercept(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebService chain)
			throws IOException, WebException;
}
