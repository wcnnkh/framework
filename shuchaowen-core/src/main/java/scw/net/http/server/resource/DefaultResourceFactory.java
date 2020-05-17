package scw.net.http.server.resource;

import java.util.Arrays;

import scw.core.GlobalPropertyFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.http.HttpMethod;
import scw.net.http.server.ServerHttpRequest;

public class DefaultResourceFactory implements ResourceFactory {
	private static Logger logger = LoggerUtils.getLogger(DefaultResourceFactory.class);

	private final String resourceRoot;
	private final String[] resourcePath;

	public DefaultResourceFactory(String resourceRoot, String[] resourcePath) {
		this.resourceRoot = StringUtils.isEmpty(resourceRoot) ? GlobalPropertyFactory.getInstance().getWorkPath()
				: resourceRoot;
		this.resourcePath = resourcePath;
		if (!ArrayUtils.isEmpty(resourcePath)) {
			logger.info("resourceRoot:{}", resourceRoot);
			logger.info("resourcePath:{}", Arrays.toString(resourcePath));
		}
	}

	public Resource getResource(ServerHttpRequest serverRequest) {
		if (ArrayUtils.isEmpty(resourcePath)) {
			return null;
		}
	
		if (HttpMethod.GET != ((ServerHttpRequest) serverRequest).getMethod()) {
			return null;
		}

		for (

		String p : resourcePath) {
			if (StringUtils.test(serverRequest.getPath(), p)) {
				return new FileResource(resourceRoot + serverRequest.getPath());
			}
		}
		return null;
	}

}
