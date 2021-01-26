package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.instance.InstanceDefinition;

public interface BeanDefinition extends InstanceDefinition{
	String getId();
	
	Collection<String> getNames();

	boolean isSingleton();

	AnnotatedElement getAnnotatedElement();

	void dependence(Object instance) throws BeansException;

	void init(Object instance) throws BeansException;

	void destroy(Object instance) throws BeansException;
}
