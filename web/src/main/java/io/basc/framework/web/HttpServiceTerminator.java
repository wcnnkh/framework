package io.basc.framework.web;

import java.io.IOException;

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
