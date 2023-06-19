package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;

public final class BeanPostProcessors extends ConfigurableServices<BeanPostProcessor> implements BeanPostProcessor {

	public BeanPostProcessors() {
		super(BeanPostProcessor.class);
	}

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		for (BeanPostProcessor beanPostProcessor : getServices()) {
			beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
		}
		BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
	}

	@Override
	public void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
		for (BeanPostProcessor beanPostProcessor : getServices()) {
			beanPostProcessor.postProcessAfterInitialization(bean, beanName);
		}
	}

	@Override
	public void postProcessBeforeDestroy(Object bean, String beanName) throws BeansException {
		for (BeanPostProcessor beanPostProcessor : getServices().reverse()) {
			beanPostProcessor.postProcessBeforeDestroy(bean, beanName);
		}
		BeanPostProcessor.super.postProcessBeforeDestroy(bean, beanName);
	}

	@Override
	public void postProcessAfterDestroy(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessAfterDestroy(bean, beanName);
		for (BeanPostProcessor beanPostProcessor : getServices().reverse()) {
			beanPostProcessor.postProcessAfterDestroy(bean, beanName);
		}
	}

}
