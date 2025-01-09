package io.basc.framework.beans.factory.component;

import java.lang.reflect.Method;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.env.EnvironmentCapable;
import io.basc.framework.core.type.share.SharableMethodMetadata;

public final class ConfigurationClassPostProcessor extends ComponentRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(ComponentResolver componentResolver, EnvironmentCapable context,
			BeanDefinitionRegistry registry) throws BeansException {
		registry.getBeanDefinitionNames().forEach((name) -> {
			BeanDefinition beanDefinition = registry.getBeanDefinition(name);
			if (!componentResolver.isConfiguration(beanDefinition)) {
				return;
			}

			for (Method method : beanDefinition.getExecutionStrategy().getReturnTypeDescriptor().getType()
					.getDeclaredMethods()) {
				SharableMethodMetadata methodMetadata = new SharableMethodMetadata(method);
				if (!componentResolver.isComponent(methodMetadata)) {
					continue;
				}

				BeanDefinition configurationBeanDefinition = componentResolver.createComponent(beanDefinition,
						methodMetadata);
				registerComponent(componentResolver, context, registry, configurationBeanDefinition);
			}
		});
	}
}
