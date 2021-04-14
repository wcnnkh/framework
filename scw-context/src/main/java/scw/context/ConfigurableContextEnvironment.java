package scw.context;

import scw.core.type.scanner.ConfigurableClassScanner;
import scw.env.ConfigurableEnvironment;

public interface ConfigurableContextEnvironment extends ContextEnvironment, ConfigurableEnvironment, ConfigurableClassScanner {
	void source(Class<?> sourceClass);

	ConfigurableClassesLoader getContextClassesLoader();
}
