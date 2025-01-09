package io.basc.framework.beans.factory.di;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.core.env.PropertyResolver;
import io.basc.framework.core.execution.resolver.PropertyFactory;
import io.basc.framework.observe.properties.ObservablePropertyFactory;
import io.basc.framework.util.Elements;
import io.basc.framework.util.exchange.Registration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class ConfigurationPropertiesBeanPostProcessor implements BeanPostProcessor {
	private final PropertyResolver propertyResolver;
	private final BeanRegistrationManager beanRegistrationManager;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		configurationProperties(bean, beanName);
	}

	public void configurationProperties(Object bean, String beanName) {
		configurationProperties(bean, beanName, getConfigurationPropertiesPrefixs(bean, beanName));
	}

	protected abstract boolean isSingleton(String beanName);

	public void configurationProperties(Object bean, String beanName, Elements<String> prefixs) {
		if (isSingleton(beanName)) {
			if (beanRegistrationManager.isRegisted(beanName)) {
				return;
			}

			Registration registration = Registration.EMPTY;
			if (propertyFactory instanceof ObservablePropertyFactory) {
				ObservablePropertyFactory observablePropertyFactory = (ObservablePropertyFactory) propertyFactory;
				registration = observablePropertyFactory.registerKeysListener((keys) -> {
					if (keys.anyMatch(prefixs, String::startsWith)) {
						transform(propertyFactory, bean, prefixs);
					}
				});
			}

			beanRegistrationManager.register(beanName, registration);
		}
		transform(propertyFactory, bean, prefixs);
	}

	protected abstract void transform(Object source, Object target, Elements<String> prefixs);

	@Override
	public void postProcessAfterDestroy(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessAfterDestroy(bean, beanName);
		if (isSingleton(beanName)) {
			beanRegistrationManager.unregister(beanName);
		}
	}

	protected abstract Elements<String> getConfigurationPropertiesPrefixs(Object bean, String beanName);
}
