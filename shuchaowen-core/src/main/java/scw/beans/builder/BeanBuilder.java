package scw.beans.builder;

import scw.core.instance.InstanceBuilder;

public interface BeanBuilder extends InstanceBuilder<Object>{
	void dependence(Object instance) throws Exception;
	
	void init(Object instance) throws Exception;
	
	void destroy(Object instance) throws Exception;
}
