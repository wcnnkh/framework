package io.basc.framework.beans;

import java.util.NoSuchElementException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Optional;

/**
 * bean的供应商
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface BeanProvider<T> extends ParameterDescriptor, Optional<T> {

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
	 * bean的构造器
	 * 
	 * @return
	 */
	@Nullable
	Executor getExecutor();

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
