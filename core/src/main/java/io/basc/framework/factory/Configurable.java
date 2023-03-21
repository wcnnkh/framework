package io.basc.framework.factory;

/**
 * 可配置的, 工厂模式应该实现此注入
 * 
 * @author wcnnkh
 */
public interface Configurable {

	boolean isConfigured();

	void configure(ServiceLoaderFactory serviceLoaderFactory);
}
