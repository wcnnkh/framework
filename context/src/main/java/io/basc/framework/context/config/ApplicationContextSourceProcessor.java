package io.basc.framework.context.config;

public interface ApplicationContextSourceProcessor<T> {
	void process(ConfigurableApplicationContext context, T source);
}
