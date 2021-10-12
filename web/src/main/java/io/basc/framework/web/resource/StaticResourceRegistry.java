package io.basc.framework.web.resource;

import java.io.IOException;

import io.basc.framework.env.Sys;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.pattern.HttpPatterns;
import io.basc.framework.web.pattern.ServerHttpRequestAccept;

public class StaticResourceRegistry extends HttpPatterns<StaticResourceResolver>
		implements HttpService, ServerHttpRequestAccept {
	private String defaultFileName = "index.html";
	private ResourceLoader resourceLoader;

	public ResourceLoader getResourceLoader() {
		return resourceLoader == null ? Sys.env : resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public String getDefaultFileName() {
		return defaultFileName;
	}

	public void setDefaultFileName(String defaultFileName) {
		this.defaultFileName = defaultFileName;
	}

	public void add(String pattern, String location) {
		add(pattern, new AbsoluteStaticResourceResolver(location));
	}

	@Override
	public boolean accept(ServerHttpRequest request) {
		Resource resource = getResource(request);
		if (resource == null || !resource.exists()) {
			return false;
		}
		request.setAttribute(Resource.class.getName(), resource);
		return true;
	}

	public Resource getResource(ServerHttpRequest request) {
		StaticResourceResolver resolver = get(request);
		if (resolver == null) {
			return null;
		}

		String path = resolver.resolve(request);
		path = StringUtils.cleanPath(path);
		if (path.endsWith("/")) {
			path += getDefaultFileName();
		}
		Assert.securePath(path);
		return getResourceLoader().getResource(path);
	}

	public MimeType getMimeType(Resource resource) {
		return FileMimeTypeUitls.getMimeType(resource);
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		Resource resource = (Resource) request.getAttribute(Resource.class.getName());
		MimeType mimeType = getMimeType(resource);
		WebUtils.writeStaticResource(request, response, resource, mimeType);
	}

}
