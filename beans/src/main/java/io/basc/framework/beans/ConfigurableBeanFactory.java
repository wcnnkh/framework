package io.basc.framework.beans;

import io.basc.framework.aop.ConfigurableAop;
import io.basc.framework.context.ConfigurableClassesLoader;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.core.parameter.ParameterFactories;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.factory.SingletonRegistry;

public interface ConfigurableBeanFactory
		extends BeanFactory, ConfigurableContext, BeanDefinitionRegistry, SingletonRegistry {
	ConfigurableClassesLoader getContextClasses();

	ConfigurableAop getAop();

	EventDispatcher<BeanlifeCycleEvent> getLifecycleDispatcher();
	
	/**
	 * 是否已经初始化
	 * @return
	 */
	boolean isInitialized();
	
	ParameterFactories getDefaultValueFactory();
}
