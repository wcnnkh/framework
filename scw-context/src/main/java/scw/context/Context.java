package scw.context;

import scw.core.type.scanner.ClassScanner;
import scw.env.Environment;
import scw.instance.ServiceLoaderFactory;

public interface Context extends ServiceLoaderFactory {
	Environment getEnvironment();

	ClassesLoader getSourceClasses();

	ClassesLoader getContextClassesLoader();

	ClassesLoaderFactory getClassesLoaderFactory();

	ClassScanner getClassScanner();
}
