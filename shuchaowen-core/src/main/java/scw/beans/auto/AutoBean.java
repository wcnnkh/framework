package scw.beans.auto;

import scw.core.instance.InstanceDefinition;

public interface AutoBean extends InstanceDefinition{
	boolean isReference();
	
	Class<?> getTargetClass();
	
	boolean isInstance();
}
