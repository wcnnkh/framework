package scw.context;

import scw.core.type.scanner.ConfigurableClassScanner;
import scw.env.ConfigurableEnvironment;

public interface ConfigurableContext extends Context {

	ConfigurableEnvironment getEnvironment();

	void source(Class<?> sourceClass);
	
	void componentScan(String packageName);

	ConfigurableClassesLoader getContextClassesLoader();

	ConfigurableClassScanner getClassScanner();
}
