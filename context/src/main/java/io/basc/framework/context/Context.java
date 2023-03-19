package io.basc.framework.context;

import io.basc.framework.env.Environment;
import io.basc.framework.util.ClassesLoader;

public interface Context extends Environment {
	ClassesLoader getSourceClasses();

	ClassesLoader getContextClasses();

	ClassesLoaderFactory getClassesLoaderFactory();

	ContextResolver getContextResolver();
}
