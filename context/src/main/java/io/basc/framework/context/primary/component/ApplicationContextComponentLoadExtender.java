package io.basc.framework.context.primary.component;

import io.basc.framework.beans.factory.config.BeanDefinitionRegistryPostProcessor;
import io.basc.framework.context.config.ApplicationContextSourceLoadExtender;
import io.basc.framework.context.config.ApplicationContextSourceLoader;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.util.collections.Elements;

public interface ApplicationContextComponentLoadExtender
		extends ApplicationContextSourceLoadExtender<Class<?>, BeanDefinitionRegistryPostProcessor> {
	@Override
	Elements<BeanDefinitionRegistryPostProcessor> load(ConfigurableApplicationContext context, Class<?> primarySource,
			ApplicationContextSourceLoader<? super Class<?>, BeanDefinitionRegistryPostProcessor> chain);
}
