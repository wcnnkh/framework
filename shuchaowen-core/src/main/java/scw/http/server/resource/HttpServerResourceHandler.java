package scw.http.server.resource;

import java.io.IOException;

import scw.http.server.HttpServiceHandler;
import scw.http.server.HttpServiceHandlerAccept;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.io.IOUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class HttpServerResourceHandler implements HttpServiceHandler, HttpServiceHandlerAccept {
	private static Logger logger = LoggerUtils.getLogger(HttpServerResourceHandler.class);
	private HttpServerResourceFactory httpServerResourceFactory;

	public HttpServerResourceHandler(HttpServerResourceFactory httpServerResourceFactory) {
		this.httpServerResourceFactory = httpServerResourceFactory;
	}

	public boolean accept(ServerHttpRequest request) {
		HttpServerResource httpServerResource = httpServerResourceFactory.getResource(request);
		if (httpServerResource != null && httpServerResource.exists()) {
			request.setAttribute(getClass().getName(), httpServerResource);
			return true;
		}
		return false;
	}

	public void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		HttpServerResource httpServerResource = (HttpServerResource) request.getAttribute(getClass().getName());
		if (httpServerResource == null) {
			httpServerResource = httpServerResourceFactory.getResource(request);
		}

		if (httpServerResource == null) {
			logger.error("not accepted: {}", request.getPath());
			return ;
		}
		IOUtils.copy(httpServerResource.getInputStream(), response.getBody());
	}
}
