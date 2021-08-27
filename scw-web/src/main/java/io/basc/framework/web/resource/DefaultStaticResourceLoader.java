package io.basc.framework.web.resource;

import io.basc.framework.core.Assert;
import io.basc.framework.core.utils.ArrayUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.env.Sys;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;

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
		return resourceLoader == null? Sys.env:resourceLoader;
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
