package scw.beans;

import scw.instance.SingletonRegistry;
import scw.util.Status;

public interface SingletonBeanRegistry extends SingletonRegistry {
	Status<Object> getSingleton(BeanDefinition definition);

	void destroyAll();
}
