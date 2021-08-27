package io.basc.framework.context;

import io.basc.framework.core.type.scanner.ConfigurableClassScanner;
import io.basc.framework.env.ConfigurableEnvironment;

public interface ConfigurableContext extends Context {

	ConfigurableEnvironment getEnvironment();

	void source(Class<?> sourceClass);
	
	void componentScan(String packageName);

	ConfigurableClassesLoader getContextClasses();

	ConfigurableClassScanner getClassScanner();
}
