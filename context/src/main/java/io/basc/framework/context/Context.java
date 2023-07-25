package io.basc.framework.context;

import io.basc.framework.env.Environment;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.spi.ServiceLoader;

public interface Context extends Environment, ClassLoaderProvider {
	ServiceLoader<Class<?>> getSourceClasses();

	ServiceLoader<Class<?>> getContextClasses();

	ClassScanner getClassScanner();
}
