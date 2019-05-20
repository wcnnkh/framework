package scw.beans;

import scw.core.Instance;

public interface BeanDefinition extends Instance{
	String getId();

	String[] getNames();

	Class<?> getType();

	boolean isSingleton();

	boolean isProxy();
	
	void autowrite(Object bean) throws Exception;

	void init(Object bean) throws Exception;

	void destroy(Object bean) throws Exception;
}
