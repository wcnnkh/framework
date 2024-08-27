package io.basc.framework.context.primary.component;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.component.LocationPatternComponentRegistryPostProcessor;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.share.SharableAnnotationMetadata;
import io.basc.framework.util.Elements;

public class PrimaryComponentRegistryPostProcessor extends LocationPatternComponentRegistryPostProcessor {
	private final Class<?> primaryClass;

	public PrimaryComponentRegistryPostProcessor(Class<?> primaryClass, Elements<String> locationPatterns) {
		super(locationPatterns);
		this.primaryClass = primaryClass;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		AnnotationMetadata annotationMetadata = new SharableAnnotationMetadata(primaryClass);
		if (getResolvers().isComponent(annotationMetadata)) {
			BeanDefinition beanDefinition = getResolvers().createComponent(annotationMetadata,
					primaryClass.getClassLoader());
			if (!registry.containsBeanDefinition(beanDefinition.getName())) {
				registry.registerBeanDefinition(beanDefinition.getName(), beanDefinition);
			}
		}
		super.postProcessBeanDefinitionRegistry(registry);
	}
}
