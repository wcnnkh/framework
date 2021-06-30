package scw.web.resource;

import java.io.IOException;

import scw.http.HttpMethod;
import scw.io.Resource;
import scw.net.MimeType;
import scw.web.HttpService;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.WebUtils;
import scw.web.pattern.ServerHttpRequestAccept;

public class StaticResourceHttpService implements HttpService, ServerHttpRequestAccept {
	private StaticResourceLoader resourceLoader;

	public StaticResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(StaticResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public boolean accept(ServerHttpRequest request) {
		if (resourceLoader == null) {
			return false;
		}

		if (request.getMethod() != HttpMethod.GET) {
			return false;
		}

		Resource resource = resourceLoader.getResource(request.getPath());
		return resource != null && resource.exists();
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		Resource resource = resourceLoader.getResource(request.getPath());
		MimeType mimeType = resourceLoader.getMimeType(resource);
		WebUtils.writeStaticResource(request, response, resource, mimeType);
	}
}
