package scw.beans;

import scw.core.instance.InstanceBuilder;

public interface BeanBuilder extends InstanceBuilder<Object>{
	void init(Object instance) throws Exception;
	
	void destroy(Object instance) throws Exception;
}
