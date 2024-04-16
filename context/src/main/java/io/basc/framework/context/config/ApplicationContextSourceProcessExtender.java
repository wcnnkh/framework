package io.basc.framework.context.config;

public interface ApplicationContextSourceProcessExtender<T> {
	void process(ConfigurableApplicationContext context, T source, ApplicationContextSourceProcessor<? super T> chain);
}
