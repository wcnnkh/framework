package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.util.ServiceRegistry;

public class BeanPostProcessorRegistry extends ServiceRegistry<BeanPostProcessor> implements BeanPostProcessor {

	@Override
	public void postProcessBeforeDependencies(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessBeforeDependencies(bean, beanName);
		for (BeanPostProcessor beanPostProcessor : getServices()) {
			beanPostProcessor.postProcessBeforeDependencies(bean, beanName);
		}
	}

	@Override
	public void postProcessAfterDependencies(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessAfterDependencies(bean, beanName);
		for (BeanPostProcessor beanPostProcessor : getServices()) {
			beanPostProcessor.postProcessAfterDependencies(bean, beanName);
		}
	}

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
		for (BeanPostProcessor beanPostProcessor : getServices()) {
			beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
		}
	}

	@Override
	public void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
		for (BeanPostProcessor beanPostProcessor : getServices()) {
			beanPostProcessor.postProcessAfterInitialization(bean, beanName);
		}
	}

	@Override
	public void postProcessAfterDestory(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessAfterDestory(bean, beanName);
		for (BeanPostProcessor beanPostProcessor : getServices()) {
			beanPostProcessor.postProcessAfterDestory(bean, beanName);
		}
	}

	@Override
	public void postProcessBeforeDestory(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessBeforeDestory(bean, beanName);
		for (BeanPostProcessor beanPostProcessor : getServices()) {
			beanPostProcessor.postProcessBeforeDestory(bean, beanName);
		}
	}
}
