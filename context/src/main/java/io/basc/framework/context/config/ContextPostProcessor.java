package io.basc.framework.context.config;

public interface ContextPostProcessor {
	void postProcessContext(ConfigurableContext context) throws Throwable;
}
