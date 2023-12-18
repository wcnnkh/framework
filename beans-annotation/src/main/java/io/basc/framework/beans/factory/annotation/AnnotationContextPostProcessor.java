package io.basc.framework.beans.factory.annotation;

import io.basc.framework.context.config.ConfigurableApplicationContext;

public class AnnotationContextPostProcessor extends AnnotationBeanDefinitionRegistryContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableApplicationContext context) throws Throwable {
		context.getBeanPostProcessors()
				.register(new AnnotationHookBeanPostProcessor(context.getParameterExtractors(), context));
		context.getBeanPostProcessors().register(new AnnotationConfigurationPropertiesBeanPostProcessor(context));
		super.postProcessContext(context);
	}

}
