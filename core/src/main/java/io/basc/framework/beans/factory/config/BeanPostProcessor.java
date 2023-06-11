package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;

public interface BeanPostProcessor {
	default void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// ignore
	}

	default void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// ignore
	}

	default void postProcessBeforeDestory(Object bean, String beanName) throws BeansException {
		// ignore
	}

	default void postProcessAfterDestory(Object bean, String beanName) throws BeansException {
		// ignore
	}
}
