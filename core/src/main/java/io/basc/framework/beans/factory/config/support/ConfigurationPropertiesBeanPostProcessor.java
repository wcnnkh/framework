package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.support.DefaultObjectMapper;
import io.basc.framework.util.Elements;
import io.basc.framework.value.PropertyFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ConfigurationPropertiesBeanPostProcessor extends DefaultObjectMapper
		implements BeanPostProcessor {
	private final PropertyFactory propertyFactory;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		configurationProperties(bean, beanName);
	}

	public void configurationProperties(Object bean, String beanName) {
		configurationProperties(bean, beanName, getConfigurationPropertiesPrefixs(bean, beanName));
	}

	public void configurationProperties(Object bean, String beanName, Elements<String> prefixs) {
		transform(propertyFactory, bean, prefixs);
	}

	public void transform(Object source, Object target, Elements<String> prefixs) {
		ConfigurationPropertiesMappingStrategy configurationPropertiesMappingStrategy = new ConfigurationPropertiesMappingStrategy();
		// TODO 还未处理
		transform(source, TypeDescriptor.forObject(source), null, target, TypeDescriptor.forObject(target), null,
				configurationPropertiesMappingStrategy);
	}

	protected abstract Elements<String> getConfigurationPropertiesPrefixs(Object bean, String beanName);
}
