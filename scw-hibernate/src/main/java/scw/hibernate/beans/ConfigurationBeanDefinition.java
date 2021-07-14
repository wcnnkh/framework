package scw.hibernate.beans;

import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.io.Resource;

public class ConfigurationBeanDefinition extends DefaultBeanDefinition {
	
	public ConfigurationBeanDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, Configuration.class);
	}

	public boolean isInstance() {
		return beanFactory.getEnvironment().exists(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME);
	}

	public Object create() throws BeansException {
		Resource resource = beanFactory.getEnvironment()
				.getResource(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME);
		try {
			return new Configuration().configure(resource.getURL());
		} catch (HibernateException e) {
			throw new BeansException(e);
		} catch (IOException e) {
			throw new BeansException(resource.getDescription(), e);
		}
	}
}