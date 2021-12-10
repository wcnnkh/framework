package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.annotation.Service;

public class ServiceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		for (Class<?> clz : beanFactory.getContextClasses()) {
			Service service = clz.getAnnotation(Service.class);
			if (service == null) {
				continue;
			}

			ServiceBeanDefinition bean = new ServiceBeanDefinition(beanFactory, clz);
			beanFactory.registerDefinition(bean);
		}
	}
}
