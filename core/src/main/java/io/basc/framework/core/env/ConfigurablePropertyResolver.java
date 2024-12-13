package io.basc.framework.core.env;

public interface ConfigurablePropertyResolver extends PropertyResolver {
	@Override
	ConfigurablePlaceholderResolver getPlaceholderReplacer();

	ConfigurableConversionService getConversionService();
}
