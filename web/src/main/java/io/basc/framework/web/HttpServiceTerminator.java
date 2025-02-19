package io.basc.framework.web;

import java.io.IOException;

import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;

public interface HttpServiceTerminator extends WebServiceTerminator {
	@Override
	default boolean test(ServerRequest serverRequest) {
		return serverRequest instanceof ServerHttpRequest && test((ServerHttpRequest) serverRequest);
	}

	boolean test(ServerHttpRequest serverHttpRequest);

	@Override
	default void service(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException, WebException {
		if (test(serverRequest) && serverResponse instanceof ServerHttpResponse) {
			service((ServerHttpRequest) serverRequest, (ServerHttpResponse) serverResponse);
		}
	}

	void service(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse)
			throws IOException, WebException;
}
