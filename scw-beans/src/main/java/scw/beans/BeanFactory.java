package scw.beans;

import scw.aop.Aop;
import scw.context.ContextLoader;
import scw.env.Environment;
import scw.event.BasicEventDispatcher;
import scw.instance.InstanceFactory;
import scw.instance.SingletonFactory;

public interface BeanFactory extends InstanceFactory, ContextLoader, BeanDefinitionFactory, SingletonFactory, BasicEventDispatcher<BeanLifeCycleEvent> {
	Environment getEnvironment();
	
	boolean isSingleton(String name);

	boolean isSingleton(Class<?> clazz);
	
	Aop getAop();
}
