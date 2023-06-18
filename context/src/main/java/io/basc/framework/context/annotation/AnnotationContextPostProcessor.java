package io.basc.framework.context.annotation;

import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.ioc.annotation.AnnotationHookBeanPostProcessor;

public class AnnotationContextPostProcessor extends AnnotationBeanDefinitionRegistryContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		context.getBeanPostProcessors().register(new AnnotationHookBeanPostProcessor(context.getParameterParser()));
		context.getBeanPostProcessors().register(new AnnotationConfigurationPropertiesBeanPostProcessor(context));
		super.postProcessContext(context);
	}

}
