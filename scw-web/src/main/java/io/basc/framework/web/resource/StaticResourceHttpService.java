package io.basc.framework.web.resource;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.io.Resource;
import io.basc.framework.net.MimeType;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.pattern.ServerHttpRequestAccept;

import java.io.IOException;

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
