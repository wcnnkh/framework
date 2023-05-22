package io.basc.framework.hibernate.beans;

import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import io.basc.framework.beans.BeansException;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentBeanDefinition;
import io.basc.framework.io.Resource;

public class ConfigurationBeanDefinition extends EnvironmentBeanDefinition {

	public ConfigurationBeanDefinition(Environment environment) {
		super(environment, Configuration.class);
	}

	public boolean isInstance() {
		return true;
	}

	public Object create() throws BeansException {
		Resource resource = getEnvironment().getResourceLoader()
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