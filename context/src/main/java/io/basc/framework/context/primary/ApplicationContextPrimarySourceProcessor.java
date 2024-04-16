package io.basc.framework.context.primary;

import io.basc.framework.context.config.ApplicationContextSourceProcessor;
import io.basc.framework.context.config.ConfigurableApplicationContext;

public interface ApplicationContextPrimarySourceProcessor extends ApplicationContextSourceProcessor<Class<?>> {
	@Override
	void process(ConfigurableApplicationContext context, Class<?> source);
}
