package scw.hibernate.beans;

import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;

import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;

public class SessionFactoryBeanDefinition extends DefaultBeanDefinition {

	public SessionFactoryBeanDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, SessionFactory.class);
	}

	public boolean isInstance() {
		return beanFactory.isInstance(org.hibernate.cfg.Configuration.class);
	}

	public Object create() throws BeansException {
		org.hibernate.cfg.Configuration configuration = beanFactory.getInstance(org.hibernate.cfg.Configuration.class);
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
