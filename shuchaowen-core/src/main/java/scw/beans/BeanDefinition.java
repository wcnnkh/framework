package scw.beans;

import scw.core.instance.definition.InstanceDefinition;

public interface BeanDefinition extends InstanceDefinition {
	String getId();

	String[] getNames();
	
	boolean isProxy();

	void init(Object bean) throws Exception;

	void destroy(Object bean) throws Exception;
}
