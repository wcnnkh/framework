package io.basc.framework.context.primary;

import io.basc.framework.context.config.ApplicationContextSourceProcessExtender;
import io.basc.framework.context.config.ApplicationContextSourceProcessor;
import io.basc.framework.context.config.ConfigurableApplicationContext;

public interface ApplicationContextPrimarySourceProcessExtender
		extends ApplicationContextSourceProcessExtender<Class<?>> {
	@Override
	void process(ConfigurableApplicationContext context, Class<?> source,
			ApplicationContextSourceProcessor<? super Class<?>> chain);
}
