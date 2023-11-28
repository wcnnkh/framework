package io.basc.framework.beans.factory;

import io.basc.framework.beans.BeansException;
import io.basc.framework.execution.Executable;

/**
 * BeanFactory生成的bean
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface FactoryBean<T> extends Executable {
	/**
	 * 是否是单例
	 * 
	 * @return
	 */
	boolean isSingleton();

	@Override
	T execute() throws BeansException;
}
