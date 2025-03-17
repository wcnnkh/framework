package run.soeasy.framework.util.io.load;

import java.net.MalformedURLException;
import java.net.URL;

import run.soeasy.framework.lang.ClassLoaderProvider;
import run.soeasy.framework.lang.DefaultClassLoaderAccessor;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.exchange.Receipt;
import run.soeasy.framework.util.io.ClassPathResource;
import run.soeasy.framework.util.io.ContextResource;
import run.soeasy.framework.util.io.FileUrlResource;
import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.io.ResourceUtils;
import run.soeasy.framework.util.io.UrlResource;
import run.soeasy.framework.util.spi.Configurable;
import run.soeasy.framework.util.spi.ServiceLoaderDiscovery;

public class DefaultResourceLoader extends DefaultClassLoaderAccessor
		implements ConfigurableResourceLoader, Configurable {
	private final ConfigurableProtocolResolver protocolResolver = new ConfigurableProtocolResolver();

	public DefaultResourceLoader() {
	}

	public DefaultResourceLoader(ClassLoader classLoader) {
		super(classLoader);
	}

	public DefaultResourceLoader(ClassLoaderProvider classLoaderProvider) {
		super(classLoaderProvider);
	}

	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		return protocolResolver.doConfigure(discovery);
	}

	public ConfigurableProtocolResolver getProtocolResolver() {
		return protocolResolver;
	}

	public Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");

		Resource resource = protocolResolver.resolve(location, this);
		if (resource != null) {
			return resource;
		}

		if (location.startsWith("/")) {
			return getResourceByPath(location);
		} else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
			return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
		} else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
			} catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				return getResourceByPath(location);
			}
		}
	}

	protected Resource getResourceByPath(String path) {
		return new ClassPathContextResource(path, getClassLoader());
	}

	protected static class ClassPathContextResource extends ClassPathResource implements ContextResource {

		public ClassPathContextResource(String path, ClassLoader classLoader) {
			super(path, classLoader);
		}

		public String getPathWithinContext() {
			return getPath();
		}

		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
			return new ClassPathContextResource(pathToUse, getClassLoader());
		}
	}
}
