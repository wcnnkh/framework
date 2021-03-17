package scw.beans;

import scw.aop.Aop;
import scw.context.ProviderLoaderFactory;
import scw.env.Environment;
import scw.event.BasicEventDispatcher;
import scw.instance.InstanceFactory;
import scw.instance.SingletonFactory;

public interface BeanFactory extends InstanceFactory, ProviderLoaderFactory, BeanDefinitionFactory, SingletonFactory, BasicEventDispatcher<BeanLifeCycleEvent> {
	Environment getEnvironment();
	
	boolean isSingleton(String name);

	boolean isSingleton(Class<?> clazz);
	
	Aop getAop();
}
