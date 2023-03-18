package io.basc.framework.context;

import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.util.Registration;

public interface ConfigurableContext extends Context, ConfigurableEnvironment {

	Registration componentScan(String packageName);

	@Override
	ConfigurableClassesLoader getContextClasses();

	@Override
	ConfigurableContextResolver getContextResolver();

	ConfigurableClassesLoader getSourceClasses();

	Registration source(Class<?> sourceClass);
}
