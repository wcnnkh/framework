package scw.http.server.resource;

import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.io.ResourceUtils;
import scw.value.property.PropertyFactory;

public class DefaultStaticResourceLoader extends AbstractStaticResourceLoader {
	private final ResourceLoader resourceLoader;

	public DefaultStaticResourceLoader(PropertyFactory propertyFactory) {
		super(propertyFactory);
		this.resourceLoader = ResourceUtils.getResourceOperations();
	}

	public DefaultStaticResourceLoader(String root, String... paths) {
		this(root, paths, null, null);
	}

	public DefaultStaticResourceLoader(String resourceRoot, String[] resourcePath, String defaultFileName,
			ResourceLoader resourceLoader) {
		super(resourceRoot, resourcePath, defaultFileName);
		this.resourceLoader = resourceLoader == null ? ResourceUtils.getResourceOperations() : resourceLoader;
	}

	@Override
	protected Resource getResourceInternal(String location) {
		return resourceLoader.getResource(location);
	}

	public ClassLoader getClassLoader() {
		return resourceLoader.getClassLoader();
	}
}
