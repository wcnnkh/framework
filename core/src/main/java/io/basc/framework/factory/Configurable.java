package io.basc.framework.factory;

/**
 * 可配置的<br/>
 * 
 * 工厂模式应该实现此注入
 * 
 * @author shuchaowen
 */
public interface Configurable {
	/**
	 * 通过工厂模式实体化的会自动调用此方法
	 * 
	 * @param serviceLoaderFactory
	 */
	void configure(ServiceLoaderFactory serviceLoaderFactory);
}
