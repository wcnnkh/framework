package scw.context;

import scw.core.type.scanner.ConfigurableClassScanner;
import scw.env.ConfigurableEnvironment;

public interface ConfigurableContext extends Context {

	ConfigurableEnvironment getEnvironment();

	void source(Class<?> sourceClass);

	ConfigurableClassesLoader getContextClassesLoader();

	ConfigurableClassScanner getClassScanner();
}
