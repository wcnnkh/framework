package scw.http.server.resource;

import java.util.Arrays;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.util.DefaultStringMatcher;
import scw.util.StringMatcher;
import scw.value.property.PropertyFactory;

public abstract class AbstractStaticResourceLoader implements StaticResourceLoader {
	final Logger logger = LoggerUtils.getLogger(getClass());
	private final StringMatcher matcher;
	private final String resourceRoot;
	private final String[] resourcePath;
	private final String defaultFileName;

	public AbstractStaticResourceLoader(PropertyFactory propertyFactory) {
		this(propertyFactory.getString("http.static.resource.root"),
				propertyFactory.getObject("http.static.resource.path", String[].class),
				propertyFactory.getString("http.static.resource.default.name"), new DefaultStringMatcher());
	}

	public AbstractStaticResourceLoader(String resourceRoot, String[] resourcePath, String defaultFileName,
			StringMatcher matcher) {
		this.matcher = matcher;
		this.resourceRoot = StringUtils.isEmpty(resourceRoot) ? "/" : StringUtils.cleanPath(resourceRoot);
		this.resourcePath = resourcePath;
		this.defaultFileName = StringUtils.isEmpty(defaultFileName) ? "index.html" : defaultFileName;
		if (!ArrayUtils.isEmpty(resourcePath)) {
			logger.info("resourceDefaultFileName:{}", this.defaultFileName);
			logger.info("resourceRoot:{}", this.resourceRoot);
			logger.info("resourcePath:{}", Arrays.toString(this.resourcePath));
		}
	}

	public Resource getResource(String location) {
		if (ArrayUtils.isEmpty(resourcePath)) {
			return null;
		}

		String locationToUse = location.endsWith("/") ? (location + defaultFileName) : location;
		for (String p : resourcePath) {
			boolean accept = matcher.isPattern(p) ? matcher.match(p, location) : p.equals(location);
			if (accept) {
				if (resourceRoot.endsWith("/")) {
					locationToUse = resourceRoot
							+ (locationToUse.startsWith("/") ? locationToUse.substring(1) : locationToUse);
				} else {
					locationToUse = resourceRoot
							+ (locationToUse.startsWith("/") ? locationToUse : ("/" + locationToUse));
				}

				locationToUse = StringUtils.cleanPath(locationToUse);
				return getResourceInternal(locationToUse);
			}
		}
		return null;
	}

	protected abstract Resource getResourceInternal(String location);

	public MimeType getMimeType(Resource resource) {
		return FileMimeTypeUitls.getMimeType(resource);
	}
}
