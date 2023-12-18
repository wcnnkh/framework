package io.basc.framework.beans.factory.annotation;

import java.util.Arrays;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.config.support.MethodHookBeanPostProcessor;
import io.basc.framework.beans.factory.support.DefinitionFactoryBean;
import io.basc.framework.execution.MethodExecutor;
import io.basc.framework.execution.param.ParameterExtractor;

class AnnotationHookBeanPostProcessor extends MethodHookBeanPostProcessor {
	private final BeanFactory beanFactory;

	public AnnotationHookBeanPostProcessor(ParameterExtractor parameterExtractor, BeanFactory beanFactory) {
		super(parameterExtractor);
		this.beanFactory = beanFactory;
	}

	@Override
	protected boolean isInitializeExecutor(MethodExecutor executor, String beanName) {
		if (beanFactory.isFactoryBean(beanName)) {
			FactoryBean<?> factoryBean = beanFactory.getFactoryBean(beanName);
			if (factoryBean instanceof DefinitionFactoryBean) {
				DefinitionFactoryBean definitionFactoryBean = (DefinitionFactoryBean) factoryBean;
				Bean bean = definitionFactoryBean.getConstructor().getReturnTypeDescriptor().getAnnotation(Bean.class);
				if (bean != null) {
					if (Arrays.asList(bean.initMethod()).contains(executor.getName())) {
						return true;
					}
				}
			}
		}
		return executor.getReturnTypeDescriptor().hasAnnotation(InitMethod.class);
	}

	@Override
	protected boolean isDestoryExecutor(MethodExecutor executor, String beanName) {
		if (beanFactory.isFactoryBean(beanName)) {
			FactoryBean<?> factoryBean = beanFactory.getFactoryBean(beanName);
			if (factoryBean instanceof DefinitionFactoryBean) {
				DefinitionFactoryBean definitionFactoryBean = (DefinitionFactoryBean) factoryBean;
				Bean bean = definitionFactoryBean.getConstructor().getReturnTypeDescriptor().getAnnotation(Bean.class);
				if (bean != null) {
					if (Arrays.asList(bean.destroyMethod()).contains(executor.getName())) {
						return true;
					}
				}
			}
		}
		return executor.getReturnTypeDescriptor().hasAnnotation(Destroy.class);
	}
}
