package io.basc.framework.beans.factory.ioc;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import lombok.Data;

@Data
public class DependencyInjectionBeanPostProcessor implements BeanPostProcessor {
	private final BeanFactory beanFactory;
	private final BeanLifecycleResolver beanLifecycleResolver;
	private final BeanPropertyResolver beanPropertyResolver;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

	}

	@Override
	public void postProcessBeforeDestroy(Object bean, String beanName) throws BeansException {

	}
}
