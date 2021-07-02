package scw.beans;

import scw.aop.ConfigurableAop;
import scw.context.ConfigurableClassesLoader;
import scw.context.ConfigurableContext;
import scw.event.EventDispatcher;
import scw.instance.SingletonRegistry;

public interface ConfigurableBeanFactory
		extends BeanFactory, ConfigurableContext, BeanDefinitionRegistry, SingletonRegistry {
	ConfigurableClassesLoader getContextClassesLoader();

	ConfigurableAop getAop();

	EventDispatcher<BeanlifeCycleEvent> getLifecycleDispatcher();
	
	/**
	 * 是否已经初始化
	 * @return
	 */
	boolean isInitialized();
}
