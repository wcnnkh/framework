package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.config.ConfigurableListableBeanFactory;
import io.basc.framework.value.PropertyFactory;

public abstract class BeanFactoryConfigurationPropertiesBeanPostProcessor
		extends ConfigurationPropertiesBeanPostProcessor {
	private final BeanFactory beanFactory;

	public BeanFactoryConfigurationPropertiesBeanPostProcessor(PropertyFactory propertyFactory,
			ConfigurableListableBeanFactory configurableBeanFactory) {
		this(propertyFactory, new BeanRegistrationManager(configurableBeanFactory), configurableBeanFactory);
	}

	public BeanFactoryConfigurationPropertiesBeanPostProcessor(PropertyFactory propertyFactory,
			BeanRegistrationManager beanRegistrationManager, BeanFactory beanFactory) {
		super(propertyFactory, beanRegistrationManager);
		this.beanFactory = beanFactory;
	}

	@Override
	protected boolean isSingleton(String beanName) {
		return beanFactory.isSingleton(beanName);
	}
}
