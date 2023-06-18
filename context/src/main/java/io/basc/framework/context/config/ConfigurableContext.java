package io.basc.framework.context.config;

import io.basc.framework.context.Context;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.execution.aop.Aop;
import io.basc.framework.util.ClassLoaderAccessor;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceRegistry;

public interface ConfigurableContext extends Context, ConfigurableEnvironment, ClassLoaderAccessor {

	Aop getAop();

	Registration componentScan(String packageName);

	@Override
	ServiceRegistry<Class<?>> getContextClasses();

	ServiceRegistry<Class<?>> getSourceClasses();

	Registration source(Class<?> sourceClass);

	@Override
	ConfigurableClassScanner getClassScanner();
}
