package scw.context;

import scw.util.ClassLoaderProvider;

public interface ClassesLoaderFactory extends ClassLoaderProvider {
	ClassesLoader getClassesLoader(String packageName);
}
