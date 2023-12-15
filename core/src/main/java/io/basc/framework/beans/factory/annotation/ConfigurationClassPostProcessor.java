package io.basc.framework.beans.factory.annotation;

import java.lang.reflect.Method;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;

public class ConfigurationClassPostProcessor extends ComponentBeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		registry.getBeanDefinitionNames().forEach((name) -> {
			BeanDefinition beanDefinition = registry.getBeanDefinition(name);
			if (!isConfiguration(beanDefinition)) {
				return;
			}

			for (Method method : beanDefinition.getReturnTypeDescriptor().getType().getDeclaredMethods()) {
				if (!isComponent(TypeDescriptor.forMethodReturnType(method))) {
					continue;
				}

				BeanDefinition configurationBeanDefinition = createBeanDefinition(beanDefinition, method);
				registry.registerBeanDefinition(configurationBeanDefinition.getName(), configurationBeanDefinition);
				Elements<String> aliasNames = getAliasNames(beanDefinition);
				aliasNames.forEach((alias) -> registry.registerAlias(beanDefinition.getName(), alias));
			}
		});
	}

}
