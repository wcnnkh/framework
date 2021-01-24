package scw.beans.support;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.annotation.Service;

public class ServiceBeanFactoryPostProcessor implements BeanFactoryPostProcessor{

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		for (Class<?> clz : beanFactory.getContextClassesLoader()) {
			Service service = clz.getAnnotation(Service.class);
			if (service == null) {
				continue;
			}

			ServiceBeanDefinition bean = new ServiceBeanDefinition(beanFactory, clz);
			beanFactory.registerBeanDefinition(bean.getId(), bean);
		}
	}
}
