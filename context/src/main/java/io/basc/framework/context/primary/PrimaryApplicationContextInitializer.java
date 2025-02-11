package io.basc.framework.context.primary;

import io.basc.framework.beans.factory.config.BeanFactoryPostProcessor;
import io.basc.framework.context.ApplicationContextInitializer;
import io.basc.framework.context.ConfigurableApplicationContext;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.element.Elements;

public class PrimaryApplicationContextInitializer extends ServiceRegistry<Class<?>>
		implements ApplicationContextInitializer {
	private static PrimaryResolvers primaryResolvers = new DefaultPrimaryResolvers();

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		for (Class<?> primaryClass : getServices()) {
			Elements<BeanFactoryPostProcessor> elements = primaryResolvers.getBeanFactoryPostProcessors(primaryClass);
			elements.forEach((e) -> e.postProcessBeanFactory(applicationContext));
		}
	}

}
