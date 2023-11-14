package io.basc.framework.context.config;

import io.basc.framework.context.ConfigurableApplicationContext;

public interface ContextPostProcessor {
	void postProcessContext(ConfigurableApplicationContext context) throws Throwable;
}
