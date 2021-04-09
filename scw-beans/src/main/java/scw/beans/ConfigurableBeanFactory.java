package scw.beans;

import scw.aop.ConfigurableAop;
import scw.context.ConfigurableClassesLoader;
import scw.context.ConfigurableContextEnvironment;
import scw.event.BasicEventDispatcher;
import scw.instance.SingletonRegistry;

public interface ConfigurableBeanFactory extends BeanFactory,
		BeanDefinitionRegistry, SingletonRegistry, BasicEventDispatcher<BeanLifeCycleEvent> {
	ConfigurableClassesLoader<?> getContextClassesLoader();

	ConfigurableContextEnvironment getEnvironment();
	
	ConfigurableAop getAop();
}
