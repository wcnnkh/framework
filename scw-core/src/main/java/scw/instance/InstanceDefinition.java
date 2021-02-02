package scw.instance;

import java.util.Iterator;

import scw.core.parameter.ParameterDescriptors;

public interface InstanceDefinition extends Iterable<ParameterDescriptors>, InstanceCreator<Object>{
	Class<?> getTargetClass();
	
	boolean isInstance();
	
	boolean isInstance(Object ...params);

	Object create(Object... params) throws InstanceException;
	
	boolean isInstance(Class<?>[] parameterTypes);
	
	Iterator<ParameterDescriptors> iterator();
}
