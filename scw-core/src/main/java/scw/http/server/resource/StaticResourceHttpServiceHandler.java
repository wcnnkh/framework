package scw.http.server.resource;

import java.io.IOException;

import scw.http.HttpMethod;
import scw.http.server.HttpServiceHandler;
import scw.http.server.HttpServiceHandlerAccept;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.net.MimeType;

public class StaticResourceHttpServiceHandler implements HttpServiceHandler, HttpServiceHandlerAccept {
	private StaticResourceLoader resourceLoader;

	/**
	 * @param root 根目录
	 * @param paths 允许的路径
	 */
	public StaticResourceHttpServiceHandler(String root, String... paths) {
		this.resourceLoader = new DefaultStaticResourceLoader(root, paths);
	}

	public StaticResourceHttpServiceHandler(StaticResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public boolean accept(ServerHttpRequest request) {
		if (request.getMethod() != HttpMethod.GET) {
			return false;
		}

		Resource resource = resourceLoader.getResource(request.getPath());
		return resource != null && resource.exists();
	}

	public void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		Resource resource = resourceLoader.getResource(request.getPath());
		MimeType mimeType = resourceLoader.getMimeType(resource);
		if (mimeType != null) {
			response.setContentType(mimeType);
		}
		
		response.getHeaders().setLastModified(resource.lastModified());
		IOUtils.copy(resource.getInputStream(), response.getBody());
	}
}
