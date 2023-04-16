package io.basc.framework.context;

import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Services;

public interface ConfigurableContext extends Context, ConfigurableEnvironment {

	Registration componentScan(String packageName);

	@Override
	Services<Class<?>> getContextClasses();

	@Override
	ConfigurableContextResolver getContextResolver();

	Services<Class<?>> getSourceClasses();

	Registration source(Class<?> sourceClass);

	@Override
	ConfigurableClassScanner getClassScanner();
}
