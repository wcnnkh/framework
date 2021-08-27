package io.basc.framework.context;

import io.basc.framework.util.ClassLoaderProvider;

public interface ClassesLoaderFactory extends ClassLoaderProvider {
	ClassesLoader getClassesLoader(String packageName);
}
