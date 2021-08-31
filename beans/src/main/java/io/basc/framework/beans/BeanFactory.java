package io.basc.framework.beans;

import io.basc.framework.aop.Aop;
import io.basc.framework.context.Context;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.SingletonFactory;

public interface BeanFactory extends InstanceFactory, Context, BeanDefinitionFactory, SingletonFactory {
	boolean isSingleton(String name);

	boolean isSingleton(Class<?> clazz);

	Aop getAop();
	
	ParameterFactory getDefaultValueFactory();
}
