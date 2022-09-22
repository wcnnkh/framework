package io.basc.framework.factory;

import io.basc.framework.event.EventDispatcher;

public interface BeanLifeCycleManager extends EventDispatcher<BeanlifeCycleEvent> {

	void dependence(Object instance, BeanDefinition definition) throws FactoryException;

	void init(Object instance, BeanDefinition definition) throws FactoryException;

	void destroy(Object instance, BeanDefinition definition) throws FactoryException;
}
