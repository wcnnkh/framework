package io.basc.framework.io;

import java.net.MalformedURLException;
import java.net.URL;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.DefaultClassLoaderAccessor;
import io.basc.framework.util.StringUtils;

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

	private boolean configured;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		protocolResolver.configure(serviceLoaderFactory);
		configured = true;
	}

	@Override
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

	@Override
	public boolean isConfigured() {
		return configured;
	}

}
