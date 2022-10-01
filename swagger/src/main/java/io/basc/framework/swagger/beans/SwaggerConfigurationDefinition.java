package io.basc.framework.swagger.beans;

import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.swagger.v3.oas.integration.SwaggerConfiguration;

public class SwaggerConfigurationDefinition extends FactoryBeanDefinition {

	public SwaggerConfigurationDefinition(BeanFactory beanFactory) {
		super(beanFactory, SwaggerConfiguration.class);
	}
}
