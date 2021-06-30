package scw.web.support;

import java.io.IOException;

import scw.core.utils.StringUtils;
import scw.env.Sys;
import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.web.HttpService;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.WebUtils;
import scw.web.pattern.HttpPatternRegistry;
import scw.web.pattern.ServerHttpRequestAccept;

public class StaticResourceRegistry extends HttpPatternRegistry<String>
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
	public void service(ServerHttpRequest request, ServerHttpResponse response)
			throws IOException {
		Resource resource = (Resource) request.getAttribute(Resource.class
				.getName());
		MimeType mimeType = getMimeType(resource);
		WebUtils.writeStaticResource(request, response, resource, mimeType);
	}

}
