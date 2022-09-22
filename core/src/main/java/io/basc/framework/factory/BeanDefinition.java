package io.basc.framework.factory;

import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;

public interface BeanDefinition extends Iterable<ParameterDescriptors>, InstanceCreator<Object, BeansException> {
	String getId();

	Collection<String> getNames();

	boolean isSingleton();
	
	TypeDescriptor getTypeDescriptor();

	boolean isInstance();

	boolean isInstance(Class<?>[] parameterTypes);
	
	boolean isInstance(Object... params);

	Object create(Object... params) throws BeansException;

	Iterator<ParameterDescriptors> iterator();

	void dependence(Object instance) throws BeansException;

	void init(Object instance) throws BeansException;

	void destroy(Object instance) throws BeansException;
}
