package io.basc.framework.context.primary;

import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.context.config.ApplicationContextInitializer;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.spi.Services;

public class PrimaryApplicationContextInitializer extends Services<Class<?>> implements ApplicationContextInitializer {
	private static PrimaryResolvers primaryResolvers = new DefaultPrimaryResolvers();

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		for (Class<?> primaryClass : this) {
			Elements<BeanFactoryPostProcessor> elements = primaryResolvers.getBeanFactoryPostProcessors(primaryClass);
			elements.forEach((e) -> e.postProcessBeanFactory(applicationContext));
		}
	}

}
