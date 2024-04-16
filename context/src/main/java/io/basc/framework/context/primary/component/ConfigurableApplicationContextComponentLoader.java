package io.basc.framework.context.primary.component;

import io.basc.framework.beans.factory.config.BeanDefinitionRegistryPostProcessor;
import io.basc.framework.context.config.ConfigurableApplicationContextSourceLoader;

public class ConfigurableApplicationContextComponentLoader extends
		ConfigurableApplicationContextSourceLoader<Class<?>, BeanDefinitionRegistryPostProcessor, ApplicationContextComponentLoader, ApplicationContextComponentLoadExtender> {

	public ConfigurableApplicationContextComponentLoader() {
		setServiceClass(ApplicationContextComponentLoader.class);
		getExtender().setServiceClass(ApplicationContextComponentLoadExtender.class);
	}
}
