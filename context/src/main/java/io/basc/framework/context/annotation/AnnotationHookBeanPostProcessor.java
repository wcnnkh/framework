package io.basc.framework.context.annotation;

import java.util.Arrays;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.config.support.MethodHookBeanPostProcessor;
import io.basc.framework.beans.factory.support.DefinitionFactoryBean;
import io.basc.framework.context.ioc.annotation.Destroy;
import io.basc.framework.context.ioc.annotation.InitMethod;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;

class AnnotationHookBeanPostProcessor extends MethodHookBeanPostProcessor {
	private final BeanFactory beanFactory;

	public AnnotationHookBeanPostProcessor(ExecutionParametersExtractor executionParametersExtractor,
			BeanFactory beanFactory) {
		super(executionParametersExtractor);
		this.beanFactory = beanFactory;
	}

	@Override
	protected boolean isInitializeExecutor(Executor executor, String beanName) {
		if (beanFactory.isFactoryBean(beanName)) {
			FactoryBean<?> factoryBean = beanFactory.getFactoryBean(beanName);
			if (factoryBean instanceof DefinitionFactoryBean) {
				DefinitionFactoryBean definitionFactoryBean = (DefinitionFactoryBean) factoryBean;
				Bean bean = definitionFactoryBean.getExecutor().getReturnType().getAnnotation(Bean.class);
				if (bean != null) {
					if (Arrays.asList(bean.initMethod()).contains(executor.getName())) {
						return true;
					}
				}
			}
		}
		return executor.getReturnType().hasAnnotation(InitMethod.class);
	}

	@Override
	protected boolean isDestoryExecutor(Executor executor, String beanName) {
		if (beanFactory.isFactoryBean(beanName)) {
			FactoryBean<?> factoryBean = beanFactory.getFactoryBean(beanName);
			if (factoryBean instanceof DefinitionFactoryBean) {
				DefinitionFactoryBean definitionFactoryBean = (DefinitionFactoryBean) factoryBean;
				Bean bean = definitionFactoryBean.getExecutor().getReturnType().getAnnotation(Bean.class);
				if (bean != null) {
					if (Arrays.asList(bean.destroyMethod()).contains(executor.getName())) {
						return true;
					}
				}
			}
		}
		return executor.getReturnType().hasAnnotation(Destroy.class);
	}
}
