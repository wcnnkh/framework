package io.basc.framework.beans;

import io.basc.framework.factory.SingletonRegistry;
import io.basc.framework.util.Status;

public interface SingletonBeanRegistry extends SingletonRegistry {
	Status<Object> getSingleton(BeanDefinition definition);

	void destroyAll();
}