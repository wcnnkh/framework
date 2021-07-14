package scw.hibernate.beans;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;

@Provider
public class HibernateBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if(beanFactory.containsDefinition(SessionFactory.class.getName())) {
			beanFactory.registerDefinition(new SessionFactoryBeanDefinition(beanFactory));
		}
		
		if(beanFactory.containsDefinition(Configuration.class.getName())) {
			beanFactory.registerDefinition(new ConfigurationBeanDefinition(beanFactory));
		}
	}

}
