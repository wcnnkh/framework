package io.basc.framework.beans.factory;

import io.basc.framework.beans.BeansException;
import io.basc.framework.convert.TypeDescriptor;

/**
 * BeanFactory生成的bean
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface FactoryBean<T> {

	/**
	 * 是否是单例
	 * 
	 * @return
	 */
	boolean isSingleton();

	T getObject() throws BeansException;

	TypeDescriptor getTypeDescriptor();
}
