package io.basc.framework.factory;

/**
 * 可配置的
 * 
 * @author shuchaowen
 *
 */
public interface Configurable {
	/**
	 * 通过实例工厂调用时会自动调用此方法
	 * 
	 * @param serviceLoaderFactory
	 */
	void configure(ServiceLoaderFactory serviceLoaderFactory);
}
