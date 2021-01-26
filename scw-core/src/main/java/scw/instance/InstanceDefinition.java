package scw.instance;

import java.util.Iterator;

import scw.core.parameter.ParameterDescriptors;

public interface InstanceDefinition extends Iterable<ParameterDescriptors>{
	Class<?> getTargetClass();
	
	boolean isInstance();
	
	Object create() throws InstanceException;
	
	boolean isInstance(Object ...params);

	Object create(Object... params) throws InstanceException;
	
	boolean isInstance(Class<?>[] parameterTypes);

	Object create(Class<?>[] parameterTypes, Object... params) throws InstanceException;
	
	Iterator<ParameterDescriptors> iterator();
}
