package io.basc.framework.context.annotation;

import io.basc.framework.context.config.ConfigurableContext;

public class AnnotationContextPostProcessor extends AnnotationBeanDefinitionRegistryContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		context.getBeanPostProcessors()
				.register(new AnnotationHookBeanPostProcessor(context.getParameterParser(), context));
		context.getBeanPostProcessors().register(new AnnotationConfigurationPropertiesBeanPostProcessor(context));
		super.postProcessContext(context);
	}

}
