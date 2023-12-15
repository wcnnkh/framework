package io.basc.framework.beans.factory.annotation;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.support.MetadataReaderRegistry;
import io.basc.framework.util.element.Elements;

public class MetadataReaderRegistryPostProcessor extends ComponentBeanDefinitionRegistryPostProcessor {
	private final MetadataReaderRegistry metadataReaderRegistry = new MetadataReaderRegistry();

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		metadataReaderRegistry.load().forEach((clazz) -> {
			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(clazz);
			if (isComponent(typeDescriptor)) {
				BeanDefinition beanDefinition = createComponent(clazz);
				registry.registerBeanDefinition(beanDefinition.getName(), beanDefinition);
				Elements<String> aliasNames = getAliasNames(beanDefinition);
				aliasNames.forEach((alias) -> registry.registerAlias(beanDefinition.getName(), alias));
			}
		});
	}

	public MetadataReaderRegistry getMetadataReaderRegistry() {
		return metadataReaderRegistry;
	}
}
