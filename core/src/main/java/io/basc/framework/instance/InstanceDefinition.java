package io.basc.framework.instance;

import io.basc.framework.core.parameter.ParameterDescriptors;

import java.util.Iterator;

public interface InstanceDefinition extends Iterable<ParameterDescriptors>, InstanceCreator<Object>{
	Class<?> getTargetClass();
	
	boolean isInstance();
	
	boolean isInstance(Object ...params);

	Object create(Object... params) throws InstanceException;
	
	boolean isInstance(Class<?>[] parameterTypes);
	
	Iterator<ParameterDescriptors> iterator();
}
