package io.basc.framework.context;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ServiceLoader;

public interface ClassesLoaderFactory extends ClassLoaderProvider {
	default ServiceLoader<Class<?>> getClassesLoader(String sourceName) {
		return getClassesLoader(sourceName, null);
	}

	ServiceLoader<Class<?>> getClassesLoader(String sourceName, @Nullable TypeFilter typeFilter);
}
