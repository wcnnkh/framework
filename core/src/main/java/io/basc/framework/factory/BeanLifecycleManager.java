package io.basc.framework.factory;

import io.basc.framework.event.EventDispatcher;

public interface BeanLifecycleManager extends EventDispatcher<BeanLifecycleEvent> {

	void dependence(Object instance, BeanDefinition definition) throws FactoryException;

	void init(Object instance, BeanDefinition definition) throws FactoryException;

	void destroy(Object instance, BeanDefinition definition) throws FactoryException;
}
