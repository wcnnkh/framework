package io.basc.framework.context;

import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.io.Resource;
import io.basc.framework.util.Services;

public interface ConfigurableContext extends Context, ConfigurableEnvironment {

	void source(Class<?> sourceClass);

	void componentScan(String packageName);

	@Override
	ConfigurableClassesLoader getContextClasses();

	@Override
	Services<Resource> getConfigurationResources();

	@Override
	ConfigurableContextResolver getContextResolver();
}
