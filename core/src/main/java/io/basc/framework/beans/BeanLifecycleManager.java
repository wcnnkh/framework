package io.basc.framework.beans;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.execution.Executor;
import io.basc.framework.factory.BeanLifecycleEvent;
import io.basc.framework.factory.FactoryException;

public interface BeanLifecycleManager {

	BroadcastEventDispatcher<BeanLifecycleEvent> getBeanLifecycleEventDispatcher();

	void dependence(String beanName, Executor executor, Object bean) throws FactoryException;

	void init(String beaName, Executor executor, Object bean) throws FactoryException;

	void destroy(String beanName, Executor executor, Object bean) throws FactoryException;
}
