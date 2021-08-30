package io.basc.framework.context;

import io.basc.framework.core.type.scanner.ClassScanner;
import io.basc.framework.env.Environment;
import io.basc.framework.factory.ServiceLoaderFactory;

public interface Context extends ServiceLoaderFactory {
	Environment getEnvironment();

	ClassesLoader getSourceClasses();

	ClassesLoader getContextClasses();

	ClassesLoaderFactory getClassesLoaderFactory();

	ClassScanner getClassScanner();
}
