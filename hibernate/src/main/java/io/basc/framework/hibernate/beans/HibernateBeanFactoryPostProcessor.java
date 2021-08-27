package io.basc.framework.hibernate.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Provider
public class HibernateBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if(!beanFactory.containsDefinition(SessionFactory.class.getName())) {
			beanFactory.registerDefinition(new SessionFactoryBeanDefinition(beanFactory));
		}
		
		if(!beanFactory.containsDefinition(Configuration.class.getName())) {
			beanFactory.registerDefinition(new ConfigurationBeanDefinition(beanFactory));
		}
	}

}
