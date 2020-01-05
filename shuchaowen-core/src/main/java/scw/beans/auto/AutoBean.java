package scw.beans.auto;

import scw.core.instance.InstanceDefinition;

public interface AutoBean extends InstanceDefinition{
	boolean isReference();
	
	Class<?> getTargetClass();
	
	void init(Object bean);
	
	void destroy(Object bean);
}
