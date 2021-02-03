package scw.beans;

import scw.instance.SingletonRegistry;
import scw.util.Result;

public interface SingletonBeanRegistry extends SingletonRegistry{
	Result<Object> getSingleton(BeanDefinition definition);
	
	void destroyAll();
}
