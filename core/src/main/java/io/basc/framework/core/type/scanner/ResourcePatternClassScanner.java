package io.basc.framework.core.type.scanner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.util.ClassUtils;

public class ResourcePatternClassScanner extends AbstractResourceClassScanner {
	static final String CLASS_RESOURCE = "**/*.class";
	private ResourcePatternResolver resourcePatternResolver;

	public ResourcePatternResolver getResourcePatternResolver(
			ResourceLoader resourceLoader, ClassLoader classLoader) {
		if (resourcePatternResolver != null) {
			return resourcePatternResolver;
		}
		return new PathMatchingResourcePatternResolver(resourceLoader);
	}

	public void setResourcePatternResolver(
			ResourcePatternResolver resourcePatternResolver) {
		this.resourcePatternResolver = resourcePatternResolver;
	}

	@Override
	protected Collection<Resource> getResources(String packageName,
			ResourceLoader resourceLoader, ClassLoader classLoader) {
		String path = ClassUtils.convertClassNameToResourcePath(packageName);
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		path = path + CLASS_RESOURCE;
		try {
			Resource[] resources = getResourcePatternResolver(resourceLoader, classLoader)
					.getResources(path);
			return resources == null? null:Arrays.asList(resources);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}