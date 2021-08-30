package io.basc.framework.hibernate.beans;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class SessionFactoryBeanDefinition extends DefaultBeanDefinition {

	public SessionFactoryBeanDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, SessionFactory.class);
	}

	public boolean isInstance() {
		return beanFactory.isInstance(Configuration.class);
	}

	public Object create() throws BeansException {
		Configuration configuration = beanFactory.getInstance(Configuration.class);
		if (beanFactory.isInstance(ServiceRegistry.class)) {
			return configuration.buildSessionFactory(beanFactory.getInstance(ServiceRegistry.class));
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
