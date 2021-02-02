package scw.beans;

import scw.aop.ConfigurableAop;
import scw.context.ConfigurableClassesLoader;
import scw.env.ConfigurableEnvironment;
import scw.event.BasicEventDispatcher;
import scw.instance.SingletonRegistry;

public interface ConfigurableBeanFactory extends BeanFactory,
		BeanDefinitionRegistry, SingletonRegistry, BasicEventDispatcher<BeanLifeCycleEvent> {
	ConfigurableClassesLoader<?> getContextClassesLoader();

	ConfigurableEnvironment getEnvironment();
	
	ConfigurableAop getAop();
}
