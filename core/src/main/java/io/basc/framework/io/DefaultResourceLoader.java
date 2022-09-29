package io.basc.framework.io;

import java.net.MalformedURLException;
import java.net.URL;

import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.StringUtils;

public class DefaultResourceLoader extends DefaultClassLoaderProvider
		implements ConfigurableResourceLoader, Configurable {
	private final ConfigurableServices<ProtocolResolver> protocolResolvers = new ConfigurableServices<>(
			ProtocolResolver.class);
	private final ConfigurableServices<ResourceLoader> resourceLoaders = new ConfigurableServices<>(
			ResourceLoader.class);

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
		protocolResolvers.configure(serviceLoaderFactory);
		resourceLoaders.configure(serviceLoaderFactory);
		configured = true;
	}

	public ConfigurableServices<ProtocolResolver> getProtocolResolvers() {
		return protocolResolvers;
	}

	public ConfigurableServices<ResourceLoader> getResourceLoaders() {
		return resourceLoaders;
	}

	public Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");

		for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
			Resource resource = protocolResolver.resolve(location, this);
			if (resource != null) {
				return resource;
			}
		}

		for (ResourceLoader resourceLoader : getResourceLoaders()) {
			Resource resource = resourceLoader.getResource(location);
			if (resource != null) {
				return resource;
			}
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
