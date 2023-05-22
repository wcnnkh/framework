package io.basc.framework.beans;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.mapper.ParameterDescriptor;

public interface FactoryBean<T> extends ParameterDescriptor {

	@Override
	default TypeDescriptor getTypeDescriptor() {
		return getExecutor().getTypeDescriptor();
	}

	/**
	 * bean的构造器
	 * 
	 * @return
	 */
	Executor getExecutor();

	/**
	 * 是否可以创建
	 * 
	 * @return
	 */
	boolean canCreated();

	boolean isSingleton();

	T getObject() throws BeansException;
}
