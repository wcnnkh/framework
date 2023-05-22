package io.basc.framework.hibernate.beans;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import io.basc.framework.beans.BeansException;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentBeanDefinition;

public class SessionFactoryBeanDefinition extends EnvironmentBeanDefinition {

	public SessionFactoryBeanDefinition(Environment environment) {
		super(environment, SessionFactory.class);
	}

	public boolean isInstance() {
		return getBeanFactory().isInstance(Configuration.class);
	}

	public Object create() throws BeansException {
		Configuration configuration = getBeanFactory().getInstance(Configuration.class);
		if (getBeanFactory().isInstance(ServiceRegistry.class)) {
			return configuration.buildSessionFactory(getBeanFactory().getInstance(ServiceRegistry.class));
		} else {
			return configuration.buildSessionFactory();
		}
	}

	@Override
	public void destroy(Object instance) throws BeansException {
		if (instance instanceof SessionFactory) {
			((SessionFactory) instance).close();
		}
		super.destroy(instance);
	}
}
