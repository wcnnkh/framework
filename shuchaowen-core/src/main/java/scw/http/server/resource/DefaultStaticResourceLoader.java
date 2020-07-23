package scw.http.server.resource;

import java.util.Arrays;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.io.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.value.property.PropertyFactory;

public class DefaultStaticResourceLoader implements StaticResourceLoader {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultStaticResourceLoader.class);
	private final String resourceRoot;
	private final String[] resourcePath;
	private final String defaultFileName;
	private final ResourceLoader resourceLoader;

	public DefaultStaticResourceLoader(PropertyFactory propertyFactory) {
		this(propertyFactory.getString("http.static.resource.root"),
				propertyFactory.getObject("http.static.resource.path",
						String[].class), propertyFactory
						.getString("http.static.resource.default.name"),
				ResourceUtils.getResourceOperations());
	}

	public DefaultStaticResourceLoader(String resourceRoot,
			String[] resourcePath, String defaultFileName,
			ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader == null ? ResourceUtils
				.getResourceOperations() : resourceLoader;
		this.resourceRoot = StringUtils.isEmpty(resourceRoot) ? "/"
				: StringUtils.cleanPath(resourceRoot);
		this.resourcePath = resourcePath;
		this.defaultFileName = StringUtils.isEmpty(defaultFileName) ? "index.html"
				: defaultFileName;
		if (!ArrayUtils.isEmpty(resourcePath)) {
			logger.info("resourceDefaultFileName:{}", this.defaultFileName);
			logger.info("resourceRoot:{}", resourceRoot);
			logger.info("resourcePath:{}", Arrays.toString(resourcePath));
		}
	}

	public Resource getResource(final String location) {
		if (ArrayUtils.isEmpty(resourcePath)) {
			return null;
		}

		String locationToUse = location.endsWith("/") ? (location + defaultFileName)
				: location;
		for (String p : resourcePath) {
			if (StringUtils.test(location, p)) {
				locationToUse = resourceRoot
						+ (locationToUse.startsWith("/") ? locationToUse
								: ("/" + locationToUse));
				locationToUse = StringUtils.cleanPath(locationToUse);
				return resourceLoader.getResource(locationToUse);
			}
		}
		return null;
	}

	public ClassLoader getClassLoader() {
		return ResourceUtils.getResourceOperations().getClassLoader();
	}

	public MimeType getMimeType(Resource resource) {
		return FileMimeTypeUitls.getMimeType(resource);
	}

}
