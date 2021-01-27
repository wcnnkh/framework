package scw.beans;

import scw.aop.Aop;
import scw.context.ContextLoader;
import scw.env.Environment;
import scw.event.BasicEventDispatcher;
import scw.instance.InstanceFactory;

public interface BeanFactory extends InstanceFactory, ContextLoader, BeanDefinitionFactory {
	Environment getEnvironment();
	
	Aop getAop();

	/**
	 * bean的生命周期事件
	 * 
	 * @return
	 */
	BasicEventDispatcher<BeanLifeCycleEvent> getBeanLifeCycleEventDispatcher();
	
	boolean isSingleton(String name);

	boolean isSingleton(Class<?> clazz);
}
