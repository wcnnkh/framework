package io.basc.framework.beans.factory;

import io.basc.framework.execution.Executor;

/**
 * BeanFactory生成的bean
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface FactoryBean<T> extends Executor {
	/**
	 * 是否是单例
	 * 
	 * @return
	 */
	boolean isSingleton();
}
