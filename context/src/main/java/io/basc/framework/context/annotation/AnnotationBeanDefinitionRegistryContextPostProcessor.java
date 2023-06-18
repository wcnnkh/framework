package io.basc.framework.context.annotation;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.config.support.BeanDefinitionRegistryContextPostProcessor;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.util.StringUtils;

class AnnotationBeanDefinitionRegistryContextPostProcessor extends BeanDefinitionRegistryContextPostProcessor {

	@Override
	protected boolean canResolveBeanDefinition(Class<?> clazz) {
		return AnnotatedElementUtils.hasAnnotation(clazz, Component.class);
	}

	@Override
	protected boolean canResolveBeanDefinition(Class<?> clazz, BeanDefinition originBeanDefinition, Method method) {
		return AnnotatedElementUtils.hasAnnotation(method, Bean.class);
	}

	@Override
	protected String getBeanName(Class<?> clazz) {
		Component component = AnnotatedElementUtils.getMergedAnnotation(clazz, Component.class);
		if (component != null && StringUtils.isNotEmpty(component.value())) {
			return component.value();
		}
		return super.getBeanName(clazz);
	}
	
}
