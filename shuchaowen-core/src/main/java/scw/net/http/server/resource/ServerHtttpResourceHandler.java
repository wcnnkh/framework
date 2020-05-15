package scw.net.http.server.resource;

import java.io.IOException;

import scw.io.IOUtils;
import scw.net.http.server.HttpServiceHandler;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public final class ServerHtttpResourceHandler implements HttpServiceHandler {
	public static final int ORDER = 800;

	private ResourceFactory resourceFactory;

	public ServerHtttpResourceHandler(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	public boolean accept(ServerHttpRequest request) {
		Resource resource = resourceFactory.getResource(request);
		return resource != null && resource.exists();
	}

	public void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		Resource resource = resourceFactory.getResource(request);
		IOUtils.copy(resource.getInputStream(), response.getBody());
	}
}
