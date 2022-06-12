package io.basc.framework.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import io.basc.framework.factory.InstanceDefinition;

public interface BeanDefinition extends InstanceDefinition {
	String getId();

	Collection<String> getNames();

	AnnotatedElement getAnnotatedElement();

	void dependence(Object instance) throws BeansException;

	void init(Object instance) throws BeansException;

	void destroy(Object instance) throws BeansException;
}
