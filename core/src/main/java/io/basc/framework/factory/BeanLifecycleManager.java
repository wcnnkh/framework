package io.basc.framework.factory;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.event.BroadcastEventDispatcher;

public interface BeanLifecycleManager extends BroadcastEventDispatcher<BeanLifecycleEvent> {

	void dependence(Object instance, BeanDefinition definition) throws FactoryException;

	void init(Object instance, BeanDefinition definition) throws FactoryException;

	void destroy(Object instance, BeanDefinition definition) throws FactoryException;
}
