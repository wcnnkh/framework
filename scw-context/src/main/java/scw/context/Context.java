package scw.context;

import java.util.Enumeration;

import scw.core.type.scanner.ClassScanner;
import scw.env.Environment;
import scw.instance.ServiceLoaderFactory;

public interface Context extends ServiceLoaderFactory {
	Environment getEnvironment();

	Enumeration<Class<?>> getSourceClasses();

	ClassesLoader getContextClassesLoader();

	ClassesLoaderFactory getClassesLoaderFactory();

	ClassScanner getClassScanner();
}
