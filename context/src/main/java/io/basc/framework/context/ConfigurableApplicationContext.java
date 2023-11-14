package io.basc.framework.context;

import io.basc.framework.context.config.ConfigurableClassScanner;
import io.basc.framework.env1.ConfigurableEnvironment;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.util.ClassLoaderAccessor;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.util.spi.Services;

public interface ConfigurableApplicationContext extends ApplicationContext, ClassLoaderAccessor {
	@Override
	ConfigurableEnvironment getEnvironment();

	Aop getAop();

	Registration componentScan(String packageName);

	@Override
	Services<Class<?>> getContextClasses();

	Services<Class<?>> getSourceClasses();

	Registration source(Class<?> sourceClass);

	@Override
	ConfigurableClassScanner getClassScanner();
}
