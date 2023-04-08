package io.basc.framework.context;

import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.util.ConfigurableServiceLoader;
import io.basc.framework.util.Registration;

public interface ConfigurableContext extends Context, ConfigurableEnvironment {

	Registration componentScan(String packageName);

	@Override
	ConfigurableServiceLoader<Class<?>> getContextClasses();

	@Override
	ConfigurableContextResolver getContextResolver();

	ConfigurableServiceLoader<Class<?>> getSourceClasses();

	Registration source(Class<?> sourceClass);
	
	@Override
	ConfigurableClassScanner getClassScanner();
}
