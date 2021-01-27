package scw.context.support;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import scw.io.DefaultResourceLoader;
import scw.io.Resource;
import scw.io.ResourceLoader;

public abstract class AbstractResourceClassesLoader<S> extends
		AbstractClassesLoader<S> {
	
	protected ResourceLoader getResourceLoader(ClassLoader classLoader) {
		return new DefaultResourceLoader(classLoader);
	}

	@Override
	protected Set<Class<S>> getClasses(ClassLoader classLoader) {
		ResourceLoader resourceLoader = getResourceLoader(classLoader);
		Collection<Resource> resources;
		try {
			resources = getResources(resourceLoader, classLoader);
			return resolve(resources, classLoader, resourceLoader, null);
		} catch (IOException e1) {
			return Collections.emptySet();
		}
	}

	protected abstract Collection<Resource> getResources(
			ResourceLoader resourceLoader, ClassLoader classLoader)
			throws IOException;
}
