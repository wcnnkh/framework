package io.basc.framework.beans;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.factory.BeanLifecycleEvent;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ParentDiscover;

public interface BeanFactory extends ServiceLoaderFactory, BeanDefinitionFactory, SingletonFactory,
		BeanLifecycleManager, ParentDiscover<BeanFactory> {

	BroadcastEventDispatcher<BeanLifecycleEvent> getBeanLifecycleEventDispatcher();

	FactoryBean<Object> getFactoryBean(String beanName);

	<T> FactoryBean<T> getFactoryBean(String beanName, Class<? extends T> requiredType) throws BeansException;

	FactoryBean<Object> getFactoryBean(String beanName, ResolvableType requiredType) throws BeansException;
	
	<T> FactoryBean<T> getFactoryBean(Class<? extends T> requiredType) throws BeansException;
	
	FactoryBean<Object> getFactoryBean(ResolvableType requiredType) throws BeansException;

	<T> Elements<? extends FactoryBean<T>> getFactoryBeans(Class<? extends T> requiredType);

	Elements<? extends FactoryBean<Object>> getFactoryBeans(ResolvableType requiredType);

	<T> T getBean(Class<? extends T> requiredType) throws BeansException;

	<T> T getBean(String beanName, Class<? extends T> requiredType) throws BeansException;

	Object getBean(String beanName, ResolvableType requiredType) throws BeansException;

	<T> void destroy(FactoryBean<T> factoryBean, T bean) throws FactoryException;
}
