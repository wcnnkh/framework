package io.basc.framework.context;

import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.type.scanner.ConfigurableClassScanner;

public interface ConfigurableContext extends Context {

	ConfigurableEnvironment getEnvironment();

	void source(Class<?> sourceClass);
	
	void componentScan(String packageName);

	ConfigurableClassesLoader getContextClasses();

	ConfigurableClassScanner getClassScanner();
}
