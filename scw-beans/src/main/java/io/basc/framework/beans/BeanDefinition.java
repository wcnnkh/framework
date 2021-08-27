package io.basc.framework.beans;

import io.basc.framework.instance.InstanceDefinition;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

public interface BeanDefinition extends InstanceDefinition{
	String getId();
	
	Collection<String> getNames();

	boolean isSingleton();

	AnnotatedElement getAnnotatedElement();

	void dependence(Object instance) throws BeansException;

	void init(Object instance) throws BeansException;

	void destroy(Object instance) throws BeansException;
}
