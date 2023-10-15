package io.basc.framework.web.support;

import java.io.IOException;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.web.HttpNotFoundHandler;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.ServerRequest;
import io.basc.framework.web.ServerResponse;
import io.basc.framework.web.WebException;
import io.basc.framework.web.WebServer;
import io.basc.framework.web.cors.Cors;
import io.basc.framework.web.cors.CorsRegistry;
import io.basc.framework.web.cors.CorsUtils;

public class DefaultWebServer extends WebServer {
	private CorsRegistry corsRegistry;

	public DefaultWebServer() {
		getTerminators().registerLast(new HttpNotFoundHandler());
	}

	@Override
	public void service(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException, WebException {
		if (corsRegistry != null && serverRequest instanceof ServerHttpRequest
				&& serverResponse instanceof ServerHttpResponse) {
			cors((ServerHttpRequest) serverRequest, (ServerHttpResponse) serverResponse);
		}
		super.service(serverRequest, serverResponse);
	}

	protected void cors(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		if (!CorsUtils.isCorsRequest(request)) {
			return;
		}

		if (!corsRegistry.test(request)) {
			return;
		}

		Cors cors = corsRegistry.process(request);
		if (cors == null) {
			return;
		}

		cors.write(request, response.getHeaders());
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (corsRegistry == null) {
			corsRegistry = serviceLoaderFactory.getBeanProvider(CorsRegistry.class).getUnique().orElse(null);
		}
		super.configure(serviceLoaderFactory);
	}
}
