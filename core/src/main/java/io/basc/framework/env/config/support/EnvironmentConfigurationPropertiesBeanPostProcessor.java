package io.basc.framework.env.config.support;

import io.basc.framework.beans.factory.config.support.BeanFactoryConfigurationPropertiesBeanPostProcessor;
import io.basc.framework.beans.factory.config.support.BeanRegistrationManager;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.Environment;

public abstract class EnvironmentConfigurationPropertiesBeanPostProcessor
		extends BeanFactoryConfigurationPropertiesBeanPostProcessor {

	public EnvironmentConfigurationPropertiesBeanPostProcessor(BeanRegistrationManager beanRegistrationManager,
			Environment environment) {
		super(environment.getProperties(), beanRegistrationManager, environment);
	}

	public EnvironmentConfigurationPropertiesBeanPostProcessor(ConfigurableEnvironment configurableEnvironment) {
		this(new BeanRegistrationManager(configurableEnvironment), configurableEnvironment);
	}
}
