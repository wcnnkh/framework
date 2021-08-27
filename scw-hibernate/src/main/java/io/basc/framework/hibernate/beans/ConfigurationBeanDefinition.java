package io.basc.framework.hibernate.beans;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.io.Resource;

import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class ConfigurationBeanDefinition extends DefaultBeanDefinition {

	public ConfigurationBeanDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, Configuration.class);
	}

	public boolean isInstance() {
		return true;
	}

	public Object create() throws BeansException {
		Resource resource = beanFactory.getEnvironment()
				.getResource(StandardServiceRegistryBuilder.DEFAULT_CFG_RESOURCE_NAME);
		Configuration configuration;
		if (resource != null && resource.exists()) {
			try {
				configuration = new Configuration().configure(resource.getURL());
			} catch (HibernateException e) {
				throw new BeansException(e);
			} catch (IOException e) {
				throw new BeansException(resource.getDescription(), e);
			}
		} else {
			configuration = new Configuration();
		}
		return configuration;
	}
}