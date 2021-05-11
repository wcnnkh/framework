package scw.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scw.core.Assert;
import scw.core.OrderComparator;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.util.ClassLoaderProvider;
import scw.util.DefaultClassLoaderProvider;

public class DefaultResourceLoader implements ConfigurableResourceLoader {
	private ClassLoaderProvider classLoaderProvider;
	private List<ProtocolResolver> protocolResolvers;
	private List<ResourceLoader> resourceLoaders;

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
			if (protocolResolvers == null) {
				protocolResolvers = new ArrayList<ProtocolResolver>(4);
			}
			protocolResolvers.add(resolver);
			Collections.sort(protocolResolvers,
					OrderComparator.INSTANCE.reversed());
		}
	}

	public Iterable<ProtocolResolver> getProtocolResolvers() {
		return new Iterable<ProtocolResolver>() {

			@Override
			public Iterator<ProtocolResolver> iterator() {
				if (protocolResolvers == null) {
					return Collections.emptyIterator();
				}
				return CollectionUtils.getIterator(protocolResolvers, true);
			}
		};
	}

	public Iterable<ResourceLoader> getResourceLoaders() {
		return new Iterable<ResourceLoader>() {

			@Override
			public Iterator<ResourceLoader> iterator() {
				if (resourceLoaders == null) {
					return Collections.emptyIterator();
				}
				return CollectionUtils.getIterator(resourceLoaders, true);
			}
		};
	}

	public void addResourceLoader(ResourceLoader resourceLoader) {
		Assert.notNull(resourceLoader, "ResourceLoader must not be null");
		synchronized (this) {
			if (resourceLoaders == null) {
				resourceLoaders = new ArrayList<ResourceLoader>(4);
			}
			resourceLoaders.add(resourceLoader);
			Collections.sort(resourceLoaders,
					OrderComparator.INSTANCE.reversed());
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
