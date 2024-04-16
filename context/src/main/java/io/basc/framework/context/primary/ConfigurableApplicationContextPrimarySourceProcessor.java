package io.basc.framework.context.primary;

import io.basc.framework.context.config.ConfigurableApplicationContextSourceProcessor;

public class ConfigurableApplicationContextPrimarySourceProcessor extends
		ConfigurableApplicationContextSourceProcessor<Class<?>, ApplicationContextPrimarySourceProcessor, ApplicationContextPrimarySourceProcessExtender>
		implements ApplicationContextPrimarySourceProcessor, ApplicationContextPrimarySourceProcessExtender {

	public ConfigurableApplicationContextPrimarySourceProcessor() {
		setServiceClass(ApplicationContextPrimarySourceProcessor.class);
		getExtender().setServiceClass(ApplicationContextPrimarySourceProcessExtender.class);
	}
}
