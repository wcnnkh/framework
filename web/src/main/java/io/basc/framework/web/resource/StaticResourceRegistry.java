package io.basc.framework.web.resource;

import java.io.IOException;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.env.Sys;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.pattern.HttpPatterns;
import io.basc.framework.web.pattern.ServerHttpRequestAccept;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class StaticResourceRegistry extends HttpPatterns<String>
		implements HttpService, ServerHttpRequestAccept, StaticResourceResolver {
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
		String location = get(request);
		if (location == null) {
			return null;
		}

		String path = request.getPath();
		if (path.lastIndexOf(".") == -1) {
			path = path + getDefaultFileName();
		}

		String realPath = StringUtils.cleanPath(location + path);
		return getResourceLoader().getResource(realPath);
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
