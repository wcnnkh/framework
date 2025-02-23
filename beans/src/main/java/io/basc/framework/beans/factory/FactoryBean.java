package io.basc.framework.beans.factory;

import io.basc.framework.beans.BeansException;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.util.function.Supplier;

/**
 * BeanFactory生成的bean
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public interface FactoryBean<T> extends Supplier<T, BeansException>, SourceDescriptor {

	/**
	 * 是否是单例
	 * 
	 * @return
	 */
	boolean isSingleton();
}
