package io.basc.framework.context.config;

import io.basc.framework.context.Context;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.util.ClassLoaderAccessor;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.util.spi.Services;

public interface ConfigurableContext extends Context, ConfigurableEnvironment, ClassLoaderAccessor {

	Aop getAop();

	Registration componentScan(String packageName);

	@Override
	Services<Class<?>> getContextClasses();

	Services<Class<?>> getSourceClasses();

	Registration source(Class<?> sourceClass);

	@Override
	ConfigurableClassScanner getClassScanner();
}
