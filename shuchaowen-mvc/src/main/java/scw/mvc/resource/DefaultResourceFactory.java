package scw.mvc.resource;

import java.util.Arrays;

import scw.core.PropertyFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.MVCUtils;
import scw.mvc.Request;
import scw.mvc.http.HttpRequest;
import scw.net.http.Method;

public class DefaultResourceFactory implements ResourceFactory {
	private static Logger logger = LoggerUtils.getLogger(DefaultResourceFactory.class);
	
	private final String resourceRoot;
	private final String[] resourcePath;
	
	public DefaultResourceFactory(PropertyFactory propertyFactory){
		this(MVCUtils.getSourceRoot(propertyFactory), MVCUtils.getResourcePaths(propertyFactory));
	}

	public DefaultResourceFactory(String resourceRoot, String[] resourcePath) {
		this.resourceRoot = resourceRoot;
		this.resourcePath = resourcePath;
		if (!StringUtils.isEmpty(resourceRoot) && !ArrayUtils.isEmpty(resourcePath)) {
			logger.info("resourceRoot:{}", resourceRoot);
			logger.info("resourcePath:{}", Arrays.toString(resourcePath));
		}
	}

	public Resource getResource(Request request) {
		if (ArrayUtils.isEmpty(resourcePath)) {
			return null;
		}

		if (request instanceof HttpRequest) {
			if (Method.GET != ((HttpRequest) request).getMethod()) {
				return null;
			}
		}

		for (String p : resourcePath) {
			if (StringUtils.test(request.getControllerPath(), p)) {
				return new FileResource(resourceRoot
						+ request.getControllerPath());
			}
		}
		return null;
	}

}
