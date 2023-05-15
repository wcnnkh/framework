package io.basc.framework.beans;

import io.basc.framework.execution.Executable;
import io.basc.framework.factory.BeansException;
import io.basc.framework.util.Elements;

public interface BeanDefinition extends Executable {
	String getId();

	Elements<String> getNames();

	boolean isSingleton();

	void dependence(Object instance) throws BeansException;

	void init(Object instance) throws BeansException;

	void destroy(Object instance) throws BeansException;

	boolean isExternal();
}
