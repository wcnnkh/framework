package io.basc.framework.core.env.config;

import io.basc.framework.core.env.PropertyResolver;

public interface ConfigurablePropertyResolver extends PropertyResolver {
	@Override
	ConfigurablePlaceholderResolver getPlaceholderReplacer();

	ConfigurableConversionService getConversionService();
}
