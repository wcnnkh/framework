package scw.beans;

import scw.aop.ConfigurableAop;
import scw.context.ConfigurableClassesLoader;
import scw.context.ConfigurableContextEnvironment;
import scw.event.EventDispatcher;
import scw.instance.SingletonRegistry;

public interface ConfigurableBeanFactory extends BeanFactory,
		BeanDefinitionRegistry, SingletonRegistry, EventDispatcher<BeanLifeCycleEvent> {
	ConfigurableClassesLoader getContextClassesLoader();

	ConfigurableContextEnvironment getEnvironment();
	
	ConfigurableAop getAop();
}
