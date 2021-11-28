package io.basc.framework.context.support;

import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public abstract class AbstractResourceClassesLoader extends
		AbstractClassesLoader {
	
	protected ResourceLoader getResourceLoader(ClassLoader classLoader) {
		return new DefaultResourceLoader(classLoader);
	}

	@Override
	protected Set<Class<?>> getClasses(ClassLoader classLoader) {
		ResourceLoader resourceLoader = getResourceLoader(classLoader);
		Collection<Resource> resources;
		try {
			resources = getResources(resourceLoader, classLoader);
			return resolve(Arrays.asList(resources), classLoader, resourceLoader, null);
		} catch (IOException e1) {
			return Collections.emptySet();
		}
	}

	protected abstract Collection<Resource> getResources(
			ResourceLoader resourceLoader, ClassLoader classLoader)
			throws IOException;
}
