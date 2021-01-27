package scw.http.server.resource;

import scw.core.Assert;
import scw.core.utils.ArrayUtils;
import scw.env.Environment;
import scw.env.SystemEnvironment;
import scw.io.Resource;
import scw.io.ResourceLoader;

public class DefaultStaticResourceLoader extends AbstractStaticResourceLoader {
	private ResourceLoader resourceLoader;
	
	public DefaultStaticResourceLoader() {
	}

	public DefaultStaticResourceLoader(Environment environment) {
		this.resourceLoader = environment;
		String[] paths = environment.getObject("http.static.resource.path", String[].class);
		if (!ArrayUtils.isEmpty(paths)) {
			String resourceRoot = environment.getString("http.static.resource.root");
			resourceRoot = resourceRoot == null ? "" : resourceRoot;
			addMapping(resourceRoot, paths);
		}
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader == null? SystemEnvironment.getInstance():resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		Assert.requiredArgument(resourceLoader != null, "resourceLoader");
		this.resourceLoader = resourceLoader;
	}

	@Override
	protected Resource getResourceInternal(String location) {
		return getResourceLoader().getResource(location);
	}
}
