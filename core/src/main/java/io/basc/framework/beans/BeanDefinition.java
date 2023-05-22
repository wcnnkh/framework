package io.basc.framework.beans;

import io.basc.framework.execution.Executable;
import io.basc.framework.execution.Executor;

public interface BeanDefinition extends Executable {
	boolean isSingleton();

	void dependence(Executor executor, Object instance) throws BeansException;

	void init(Executor executor, Object instance) throws BeansException;

	void destroy(Executor executor, Object instance) throws BeansException;
}
