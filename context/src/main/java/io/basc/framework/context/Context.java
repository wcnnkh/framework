package io.basc.framework.context;

import io.basc.framework.env.Environment;
import io.basc.framework.instance.ServiceLoaderFactory;
import io.basc.framework.type.scanner.ClassScanner;

public interface Context extends ServiceLoaderFactory {
	Environment getEnvironment();

	ClassesLoader getSourceClasses();

	ClassesLoader getContextClasses();

	ClassesLoaderFactory getClassesLoaderFactory();

	ClassScanner getClassScanner();
}
