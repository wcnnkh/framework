package scw.beans;

import scw.core.instance.InstanceDefinition;

public interface BeanDefinition extends InstanceDefinition{
	String getId();

	String[] getNames();

	Class<?> getType();
	
	boolean isProxy();

	boolean isSingleton();
	
	void autowrite(Object bean) throws Exception;

	void init(Object bean) throws Exception;

	void destroy(Object bean) throws Exception;
}
