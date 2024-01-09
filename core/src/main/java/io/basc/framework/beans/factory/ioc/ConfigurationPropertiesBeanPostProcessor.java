package io.basc.framework.beans.factory.ioc;

import io.basc.framework.beans.BeanMapper;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.observe.properties.ObservablePropertyFactory;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.PropertyFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ConfigurationPropertiesBeanPostProcessor extends BeanMapper implements BeanPostProcessor {
	private final PropertyFactory propertyFactory;
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

	public void transform(Object source, Object target, Elements<String> prefixs) {
		ConfigurationPropertiesMappingStrategy configurationPropertiesMappingStrategy = new ConfigurationPropertiesMappingStrategy();
		// TODO 还未处理
		transform(source, TypeDescriptor.forObject(source), null, target, TypeDescriptor.forObject(target), null,
				configurationPropertiesMappingStrategy);
	}

	@Override
	public void postProcessAfterDestroy(Object bean, String beanName) throws BeansException {
		BeanPostProcessor.super.postProcessAfterDestroy(bean, beanName);
		if (isSingleton(beanName)) {
			beanRegistrationManager.unregister(beanName);
		}
	}

	protected abstract Elements<String> getConfigurationPropertiesPrefixs(Object bean, String beanName);
}
