package io.basc.framework.beans.factory;

import java.util.function.Supplier;

import io.basc.framework.core.ResolvableType;

/**
 * BeanFactory生成的bean
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface FactoryBean<T> extends Supplier<T> {
	ResolvableType getType();

	/**
	 * 是否是单例
	 * 
	 * @return
	 */
	boolean isSingleton();

	@Override
	T get();
}
