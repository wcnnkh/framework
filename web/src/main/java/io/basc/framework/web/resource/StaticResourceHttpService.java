package io.basc.framework.web.resource;

import java.io.IOException;
import java.util.LinkedList;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.io.Resource;
import io.basc.framework.net.MimeType;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.pattern.ServerHttpRequestAccept;

public class StaticResourceHttpService extends LinkedList<StaticResourceLoader>
		implements HttpService, ServerHttpRequestAccept {
	private static final long serialVersionUID = 1L;
	
	public StaticResourceHttpService() {
	}

	public StaticResourceHttpService(ServiceLoaderFactory serviceLoaderFactory) {
		addAll(serviceLoaderFactory.getServiceLoader(StaticResourceLoader.class).toList());
	}

	public boolean test(ServerHttpRequest request) {
		if (request.getMethod() != HttpMethod.GET) {
			return false;
		}

		for (StaticResourceLoader resolver : this) {
			Resource resource = resolver.getResource(request);
			if (resource != null && resource.exists()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		for (StaticResourceLoader resolver : this) {
			Resource resource = resolver.getResource(request);
			if (resource != null && resource.exists()) {
				MimeType mimeType = resolver.getMimeType(resource);
				WebUtils.writeStaticResource(request, response, resource, mimeType);
				break;
			}
		}
	}
}
