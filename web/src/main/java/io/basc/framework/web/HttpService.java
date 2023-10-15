package io.basc.framework.web;

import java.io.IOException;

@FunctionalInterface
public interface HttpService extends WebService {
	@Override
	default void service(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException, WebException {
		if (serverRequest instanceof ServerHttpRequest && serverResponse instanceof ServerHttpResponse) {
			service((ServerHttpRequest) serverRequest, (ServerHttpResponse) serverResponse);
		}
	}

	void service(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse)
			throws IOException, WebException;
}
