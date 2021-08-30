package io.basc.framework.context.support;

import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ResourcePatternClassesLoader extends
		AbstractResourceClassesLoader {
	static final String CLASSES_SUFFIX = "**/*" + SUFFIX;
	private final String locationPattern;

	public ResourcePatternClassesLoader(String locationPattern) {
		this.locationPattern = locationPattern.endsWith(SUFFIX) ? locationPattern
				: (locationPattern + CLASSES_SUFFIX);
	}

	public ResourcePatternResolver getResourcePatternResolver(
			ResourceLoader resourceLoader, ClassLoader classLoader) {
		return new PathMatchingResourcePatternResolver(resourceLoader);
	}

	@Override
	protected Collection<Resource> getResources(ResourceLoader resourceLoader,
			ClassLoader classLoader) throws IOException {
		Resource[] resources = getResourcePatternResolver(resourceLoader,
				classLoader).getResources(locationPattern);
		if (resources == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(resources);
	}
}
