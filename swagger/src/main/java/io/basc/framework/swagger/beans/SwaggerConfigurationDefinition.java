package io.basc.framework.swagger.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.swagger.v3.oas.integration.SwaggerConfiguration;

public class SwaggerConfigurationDefinition extends DefaultBeanDefinition {

	public SwaggerConfigurationDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, SwaggerConfiguration.class);
	}
}
