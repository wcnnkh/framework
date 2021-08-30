package io.basc.framework.io;

import io.basc.framework.instance.Configurable;
import io.basc.framework.instance.ConfigurableServices;
import io.basc.framework.instance.ServiceLoaderFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class DefaultResourceLoader implements ConfigurableResourceLoader, Configurable {
	private ClassLoaderProvider classLoaderProvider;
	private ConfigurableServices<ProtocolResolver> protocolResolvers = new ConfigurableServices<>(ProtocolResolver.class);
	private ConfigurableServices<ResourceLoader> resourceLoaders = new ConfigurableServices<>(ResourceLoader.class);

	public DefaultResourceLoader() {
	}

	public DefaultResourceLoader(ClassLoader classLoader) {
		this(new DefaultClassLoaderProvider(classLoader));
	}

	public DefaultResourceLoader(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	public void addProtocolResolver(ProtocolResolver resolver) {
		Assert.notNull(resolver, "ProtocolResolver must not be null");
		synchronized (this) {
			protocolResolvers.addService(resolver);
		}
	}
	
	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		protocolResolvers.configure(serviceLoaderFactory);
		resourceLoaders.configure(serviceLoaderFactory);
	}

	public Iterable<ProtocolResolver> getProtocolResolvers() {
		return protocolResolvers;
	}

	public Iterable<ResourceLoader> getResourceLoaders() {
		return resourceLoaders;
	}

	public void addResourceLoader(ResourceLoader resourceLoader) {
		Assert.notNull(resourceLoader, "ResourceLoader must not be null");
		synchronized (this) {
			resourceLoaders.addService(resourceLoader);
		}
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
			return new ClassPathResource(
					location.substring(CLASSPATH_URL_PREFIX.length()),
					getClassLoader());
		} else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return new UrlResource(url);
			} catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				return getResourceByPath(location);
			}
		}
	}

	protected Resource getResourceByPath(String path) {
		return new ClassPathContextResource(path, getClassLoader());
	}

	protected static class ClassPathContextResource extends ClassPathResource
			implements ContextResource {

		public ClassPathContextResource(String path, ClassLoader classLoader) {
			super(path, classLoader);
		}

		public String getPathWithinContext() {
			return getPath();
		}

		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(),
					relativePath);
			return new ClassPathContextResource(pathToUse, getClassLoader());
		}
	}

}
