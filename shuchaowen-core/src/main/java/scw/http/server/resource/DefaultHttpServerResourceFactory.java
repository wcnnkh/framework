package scw.http.server.resource;

import java.util.Arrays;

import scw.core.GlobalPropertyFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.http.server.ServerHttpRequest;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.value.property.PropertyFactory;

public class DefaultHttpServerResourceFactory implements HttpServerResourceFactory {
	private static Logger logger = LoggerUtils.getLogger(DefaultHttpServerResourceFactory.class);

	private final String resourceRoot;
	private final String[] resourcePath;

	public DefaultHttpServerResourceFactory(PropertyFactory propertyFactory) {
		this(propertyFactory.getString("server.http.resource.root"),
				propertyFactory.getObject("server.http.resource.path", String[].class));
	}

	public DefaultHttpServerResourceFactory(String resourceRoot, String[] resourcePath) {
		this.resourceRoot = StringUtils.isEmpty(resourceRoot) ? GlobalPropertyFactory.getInstance().getWorkPath()
				: resourceRoot;
		this.resourcePath = resourcePath;
		if (!ArrayUtils.isEmpty(resourcePath)) {
			logger.info("resourceRoot:{}", resourceRoot);
			logger.info("resourcePath:{}", Arrays.toString(resourcePath));
		}
	}

	public HttpServerResource getResource(ServerHttpRequest serverRequest) {
		if (ArrayUtils.isEmpty(resourcePath)) {
			return null;
		}

		if (HttpMethod.GET != ((ServerHttpRequest) serverRequest).getMethod()) {
			return null;
		}

		for (

		String p : resourcePath) {
			if (StringUtils.test(serverRequest.getPath(), p)) {
				return new HttpServerFileResource(resourceRoot + serverRequest.getPath());
			}
		}
		return null;
	}
}
