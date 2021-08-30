package io.basc.framework.type.scanner;

import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.type.filter.TypeFilter;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractResourceClassScanner extends ClassResolver
		implements ClassScanner {

	protected ResourceLoader getResourceLoader(ClassLoader classLoader) {
		return new DefaultResourceLoader(classLoader);
	}

	protected abstract Resource[] getResources(String packageName,
			ResourceLoader resourceLoader, ClassLoader classLoader);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<Class<?>> getClasses(String packageName,
			ClassLoader classLoader, TypeFilter typeFilter) {
		ResourceLoader resourceLoader = getResourceLoader(classLoader);
		Resource[] resources = getResources(packageName, resourceLoader,
				classLoader);
		if (resources == null) {
			return Collections.emptySet();
		}

		Set classes = resolve(resources, classLoader, resourceLoader,
				typeFilter);
		return classes;
	}

}
