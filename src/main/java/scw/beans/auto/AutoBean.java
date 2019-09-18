package scw.beans.auto;

import scw.core.InstanceDefinition;

public interface AutoBean extends InstanceDefinition{
	boolean isReference();
	
	Class<?> getTargetClass();
	
	boolean isInstance();
}
