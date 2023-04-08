package io.basc.framework.context;

import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.util.CacheServiceLoader;
import io.basc.framework.util.Registration;

public interface ConfigurableContext extends Context, ConfigurableEnvironment {

	Registration componentScan(String packageName);

	@Override
	CacheServiceLoader<Class<?>> getContextClasses();

	@Override
	ConfigurableContextResolver getContextResolver();

	CacheServiceLoader<Class<?>> getSourceClasses();

	Registration source(Class<?> sourceClass);
	
	@Override
	ConfigurableClassScanner getClassScanner();
}
