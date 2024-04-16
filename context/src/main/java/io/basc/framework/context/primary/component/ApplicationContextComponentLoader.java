package io.basc.framework.context.primary.component;

import io.basc.framework.beans.factory.config.BeanDefinitionRegistryPostProcessor;
import io.basc.framework.context.config.ApplicationContextSourceLoader;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.util.element.Elements;

/**
 * 组件扫描
 * 
 * @author shuchaowen
 *
 */
public interface ApplicationContextComponentLoader
		extends ApplicationContextSourceLoader<Class<?>, BeanDefinitionRegistryPostProcessor> {
	@Override
	Elements<BeanDefinitionRegistryPostProcessor> load(ConfigurableApplicationContext context, Class<?> source);
}
