package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.value.DynamicPropertyFactory;

public abstract class ConfigurationDynamicPropertiesBeanPostProcessor extends ConfigurationPropertiesBeanPostProcessor {
	private final BeanRegistrationManager beanRegistrationManager;

	public ConfigurationDynamicPropertiesBeanPostProcessor(DynamicPropertyFactory dynamicPropertyFactory,
			BeanRegistrationManager beanRegistrationManager) {
		super(dynamicPropertyFactory);
		this.beanRegistrationManager = beanRegistrationManager;
	}

	@Override
	public DynamicPropertyFactory getPropertyFactory() {
		return (DynamicPropertyFactory) super.getPropertyFactory();
	}

	protected abstract boolean isSingleton(String beanName);

	@Override
	public void configurationProperties(Object bean, String beanName, Elements<String> prefixs) {
		if (isSingleton(beanName)) {
			if (beanRegistrationManager.isRegisted(beanName)) {
				return;
			}

			Registration registration = getPropertyFactory().getKeyEventRegistry().registerListener((event) -> {
				if (event.getSource().anyMatch(prefixs, String::startsWith)) {
					super.configurationProperties(bean, beanName, prefixs);
				}
			});
			beanRegistrationManager.register(beanName, registration);
		}
		super.configurationProperties(bean, beanName, prefixs);
	}

	@Override
	public void postProcessAfterDestory(Object bean, String beanName) throws BeansException {
		if (isSingleton(beanName)) {
			beanRegistrationManager.unregister(beanName);
		}
		super.postProcessAfterDestory(bean, beanName);
	}
}
