package io.basc.framework.context.config;

import io.basc.framework.context.ConfigurableApplicationContext;

/**
 * 应用上下文初始化
 */
public interface ApplicationContextInitializer {
	/**
	 * Initialize the given application context.
	 * 
	 * @param applicationContext the application to configure
	 */
	void initialize(ConfigurableApplicationContext applicationContext);
}
