package io.basc.framework.context.annotation;

import io.basc.framework.beans.factory.config.support.BeanRegistrationManager;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.Environment;
import io.basc.framework.env.config.support.EnvironmentConfigurationPropertiesBeanPostProcessor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

class AnnotationConfigurationPropertiesBeanPostProcessor
		extends EnvironmentConfigurationPropertiesBeanPostProcessor {

	public AnnotationConfigurationPropertiesBeanPostProcessor(BeanRegistrationManager beanRegistrationManager,
			Environment environment) {
		super(beanRegistrationManager, environment);
	}

	public AnnotationConfigurationPropertiesBeanPostProcessor(ConfigurableEnvironment configurableEnvironment) {
		super(configurableEnvironment);
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
