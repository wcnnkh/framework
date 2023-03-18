package io.basc.framework.context;

import io.basc.framework.env.Environment;

public interface Context extends Environment {
	ClassesLoader getSourceClasses();

	ClassesLoader getContextClasses();

	ClassesLoaderFactory getClassesLoaderFactory();

	ContextResolver getContextResolver();
}
