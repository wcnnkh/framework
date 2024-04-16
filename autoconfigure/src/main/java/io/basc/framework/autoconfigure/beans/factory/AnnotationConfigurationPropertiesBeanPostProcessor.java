package io.basc.framework.autoconfigure.beans.factory;

import io.basc.framework.beans.factory.config.ConfigurableListableBeanFactory;
import io.basc.framework.beans.factory.ioc.BeanFactoryConfigurationPropertiesBeanPostProcessor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.PropertyFactory;

class AnnotationConfigurationPropertiesBeanPostProcessor extends BeanFactoryConfigurationPropertiesBeanPostProcessor {

	public AnnotationConfigurationPropertiesBeanPostProcessor(PropertyFactory propertyFactory,
			ConfigurableListableBeanFactory configurableBeanFactory) {
		super(propertyFactory, configurableBeanFactory);
	}

	@Override
	protected Elements<String> getConfigurationPropertiesPrefixs(Object bean, String beanName) {
		ConfigurationProperties configurationProperties = bean.getClass().getAnnotation(ConfigurationProperties.class);
		if (configurationProperties == null) {
			return Elements.empty();
		}

		String prefix = configurationProperties.prefix();
		if (StringUtils.isEmpty(prefix)) {
			prefix = configurationProperties.value();
		}

		return StringUtils.isEmpty(prefix) ? Elements.empty() : Elements.singleton(prefix);
	}

}
