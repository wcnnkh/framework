package io.basc.framework.core.type.scanner;

import java.util.Collection;
import java.util.function.Predicate;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;

public abstract class AbstractResourceClassScanner extends ClassResolver implements ClassScanner {
	protected ResourceLoader getResourceLoader(ClassLoader classLoader) {
		return new DefaultResourceLoader(classLoader);
	}

	protected abstract Collection<Resource> getResources(String packageName, ResourceLoader resourceLoader,
			ClassLoader classLoader);

	@Override
	public void scan(String packageName, ClassLoader classLoader, TypeFilter typeFilter,
			Predicate<Class<?>> predicate) {
		ResourceLoader resourceLoader = getResourceLoader(classLoader);
		Collection<Resource> resources = getResources(packageName, resourceLoader, classLoader);
		if (resources == null) {
			return;
		}

		resolve(resources, resourceLoader, classLoader, typeFilter, predicate);
	}

}
