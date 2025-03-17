package run.soeasy.framework.beans.factory;

import run.soeasy.framework.beans.BeansException;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.util.function.Supplier;

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
