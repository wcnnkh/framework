package io.basc.framework.context.ioc;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;

public class IocProcessor2 implements BeanPostProcessor {
	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		
	}
}
