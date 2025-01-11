package io.basc.framework.util.io.load;

import java.net.MalformedURLException;
import java.net.URL;

import io.basc.framework.lang.ClassLoaderProvider;
import io.basc.framework.lang.DefaultClassLoaderAccessor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.io.ClassPathResource;
import io.basc.framework.util.io.ContextResource;
import io.basc.framework.util.io.FileUrlResource;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.ResourceUtils;
import io.basc.framework.util.io.UrlResource;
import io.basc.framework.util.spi.Configurable;
import io.basc.framework.util.spi.ServiceLoaderDiscovery;

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
