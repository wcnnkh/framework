package io.basc.framework.beans;

import java.util.NoSuchElementException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Optional;

public interface FactoryBean<T> extends ParameterDescriptor, Optional<T> {

	/**
	 * bean的类型描述
	 */
	@Override
	TypeDescriptor getTypeDescriptor();

	/**
	 * 作用域
	 * 
	 * @return
	 */
	Scope getScope();

	/**
	 * 是否是单例
	 * 
	 * @return
	 */
	boolean isSingleton();
	
	@Override
	default T get() throws NoSuchElementException, BeansException {
		return Optional.super.get();
	}
}
