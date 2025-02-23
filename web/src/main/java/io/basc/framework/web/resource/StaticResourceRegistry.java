package io.basc.framework.web.resource;

import java.io.IOException;

import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.http.server.ServerHttpResponse;
import io.basc.framework.util.io.FileMimeTypeUitls;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.load.DefaultResourceLoader;
import io.basc.framework.util.io.load.ResourceLoader;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.pattern.PathRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class StaticResourceRegistry extends PathRegistry implements HttpService {
	@NonNull
	private String defaultFileName = "index.html";
	@NonNull
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Override
	public boolean test(ServerHttpRequest request) {
		Resource resource = getResource(request);
		if (resource == null || !resource.exists()) {
			return false;
		}
		request.setAttribute(Resource.class.getName(), resource);
		return true;
	}

	public Resource getResource(ServerHttpRequest request) {
		String path = process(request);
		if (path == null) {
			return null;
		}

		if (path.endsWith("/")) {
			path += getDefaultFileName();
		}
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
