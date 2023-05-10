package io.basc.framework.beans;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeansException;

public interface BeanDefinition extends Executable {
	String getId();

	Collection<String> getNames();

	boolean isSingleton();

	TypeDescriptor getTypeDescriptor();

	void dependence(Object instance) throws BeansException;

	void init(Object instance) throws BeansException;

	void destroy(Object instance) throws BeansException;

	boolean isExternal();
}
