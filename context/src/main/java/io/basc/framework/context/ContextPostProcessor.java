package io.basc.framework.context;

public interface ContextPostProcessor {
	void postProcessContext(ConfigurableContext context) throws Throwable;
}
