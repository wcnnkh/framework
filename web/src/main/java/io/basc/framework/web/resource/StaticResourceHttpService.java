package io.basc.framework.web.resource;

import java.io.IOException;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.net.MimeType;
import io.basc.framework.util.io.Resource;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.pattern.ServerHttpRequestAccept;

public class StaticResourceHttpService extends ConfigurableServices<StaticResourceLoader>
		implements HttpService, ServerHttpRequestAccept {

	public StaticResourceHttpService() {
		super(StaticResourceLoader.class);
	}

	public boolean test(ServerHttpRequest request) {
		if (request.getMethod() != HttpMethod.GET) {
			return false;
		}

		for (StaticResourceLoader resolver : getServices()) {
			Resource resource = resolver.getResource(request);
			if (resource != null && resource.exists()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		for (StaticResourceLoader resolver : getServices()) {
			Resource resource = resolver.getResource(request);
			if (resource != null && resource.exists()) {
				MimeType mimeType = resolver.getMimeType(resource);
				WebUtils.writeStaticResource(request, response, resource, mimeType);
				break;
			}
		}
	}
}
