package io.basc.framework.hibernate.beans;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.EnvironmentPostProcessor;

@Provider
public class HibernateBeanFactoryPostProcessor implements EnvironmentPostProcessor {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment) {
		if (!environment.containsDefinition(SessionFactory.class.getName())) {
			environment.registerDefinition(new SessionFactoryBeanDefinition(environment));
		}

		if (!environment.containsDefinition(Configuration.class.getName())) {
			environment.registerDefinition(new ConfigurationBeanDefinition(environment));
		}
	}

}
