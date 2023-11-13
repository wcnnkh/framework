package io.basc.framework.context.config;

public interface ContextPostProcessor {
	void postProcessContext(ConfigurableApplicationContext context) throws Throwable;
}
