package io.basc.framework.context;

import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;

public interface Context extends Environment {

	ClassesLoader getContextClasses();

	ClassesLoaderFactory getClassesLoaderFactory();

	Iterable<Resource> getConfigurationResources();

	ContextResolver getContextResolver();
}
