package io.basc.framework.hibernate.beans;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;

@Provider
public class HibernateContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		if (!context.containsDefinition(SessionFactory.class.getName())) {
			context.registerDefinition(new SessionFactoryBeanDefinition(context));
		}

		if (!context.containsDefinition(Configuration.class.getName())) {
			context.registerDefinition(new ConfigurationBeanDefinition(context));
		}
	}

}
