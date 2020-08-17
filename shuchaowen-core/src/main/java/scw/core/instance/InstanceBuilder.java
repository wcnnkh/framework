package scw.core.instance;

import java.util.Iterator;

import scw.core.parameter.ParameterDescriptors;

public interface InstanceBuilder<T> extends Iterable<ParameterDescriptors>{
	Class<? extends T> getTargetClass();
	
	boolean isInstance();
	
	T create() throws Exception;

	T create(Object... params) throws Exception;

	T create(Class<?>[] parameterTypes, Object... params) throws Exception;
	
	Iterator<ParameterDescriptors> iterator();
}
