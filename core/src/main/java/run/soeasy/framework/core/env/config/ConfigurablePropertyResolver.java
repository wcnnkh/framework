package run.soeasy.framework.core.env.config;

import run.soeasy.framework.core.env.PropertyResolver;

public interface ConfigurablePropertyResolver extends PropertyResolver {
	@Override
	ConfigurablePlaceholderResolver getPlaceholderReplacer();

	ConfigurableConversionService getConversionService();
}
