package io.basc.framework.context;

import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassesLoader;

public interface ClassesLoaderFactory extends ClassLoaderProvider {
	default ClassesLoader getClassesLoader(String sourceName) {
		return getClassesLoader(sourceName, null);
	}

	ClassesLoader getClassesLoader(String sourceName, @Nullable TypeFilter typeFilter);
}
