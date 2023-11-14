package io.basc.framework.context.annotation;

import io.basc.framework.context.ConfigurableApplicationContext;

public class AnnotationContextPostProcessor extends AnnotationBeanDefinitionRegistryContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableApplicationContext context) throws Throwable {
		context.getBeanPostProcessors()
				.register(new AnnotationHookBeanPostProcessor(context.getParameterExtractors(), context));
		context.getBeanPostProcessors().register(new AnnotationConfigurationPropertiesBeanPostProcessor(context));
		super.postProcessContext(context);
	}

}
