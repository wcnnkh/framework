package scw.beans;

import scw.aop.Aop;
import scw.context.Context;
import scw.instance.InstanceFactory;
import scw.instance.SingletonFactory;

public interface BeanFactory extends InstanceFactory, Context, BeanDefinitionFactory, SingletonFactory {
	boolean isSingleton(String name);

	boolean isSingleton(Class<?> clazz);

	Aop getAop();
}
