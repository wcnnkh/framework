package io.basc.framework.context;

import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceRegistry;

public interface ConfigurableContext extends Context, ConfigurableEnvironment {

	Registration componentScan(String packageName);

	@Override
	ServiceRegistry<Class<?>> getContextClasses();

	@Override
	ConfigurableContextResolver getContextResolver();

	ServiceRegistry<Class<?>> getSourceClasses();

	Registration source(Class<?> sourceClass);

	@Override
	ConfigurableClassScanner getClassScanner();
}
