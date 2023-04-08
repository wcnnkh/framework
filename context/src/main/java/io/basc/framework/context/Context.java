package io.basc.framework.context;

import io.basc.framework.env.Environment;
import io.basc.framework.util.ServiceLoader;

public interface Context extends Environment {
	ServiceLoader<Class<?>> getSourceClasses();

	ServiceLoader<Class<?>> getContextClasses();

	ClassesLoaderFactory getClassesLoaderFactory();

	ContextResolver getContextResolver();
}
