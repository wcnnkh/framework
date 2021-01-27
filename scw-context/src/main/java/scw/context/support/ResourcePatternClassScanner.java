package scw.context.support;

import java.io.IOException;

import scw.core.utils.ClassUtils;
import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.io.ResourcePatternResolver;
import scw.io.support.PathMatchingResourcePatternResolver;

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
	protected Resource[] getResources(String packageName,
			ResourceLoader resourceLoader, ClassLoader classLoader) {
		String path = ClassUtils.convertClassNameToResourcePath(packageName);
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		path = path + CLASS_RESOURCE;
		try {
			return getResourcePatternResolver(resourceLoader, classLoader)
					.getResources(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}