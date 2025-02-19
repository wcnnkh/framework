package io.basc.framework.web;

import java.io.IOException;

import io.basc.framework.http.HttpStatus;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.http.server.cors.CorsUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class HttpNotFoundHandler implements HttpServiceTerminator {
	private static Logger logger = LogManager.getLogger(HttpNotFoundHandler.class);

	@Override
	public boolean test(ServerHttpRequest serverHttpRequest) {
		return true;
	}

	@Override
	public void service(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse)
			throws IOException, WebException {
		if (CorsUtils.isPreFlightRequest(serverHttpRequest)) {
			return;
		}

		serverHttpResponse.sendError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
		logger.error("Not found {}", serverHttpRequest.toString());
	}

}
