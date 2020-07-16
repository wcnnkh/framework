package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

import scw.core.instance.InstanceBuilder;

public interface BeanDefinition extends InstanceBuilder<Object>{
	String getId();

	Collection<String> getNames();

	boolean isSingleton();

	AnnotatedElement getAnnotatedElement();

	void dependence(Object instance) throws Exception;

	void init(Object instance) throws Exception;

	void destroy(Object instance) throws Exception;
}
