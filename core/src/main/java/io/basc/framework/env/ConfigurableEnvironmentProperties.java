package io.basc.framework.env;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.util.placeholder.support.DefaultPlaceholderReplacer;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.ConfigurationCenter;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class ConfigurableEnvironmentProperties extends ConfigurationCenter implements EnvironmentProperties {
	private class AnyFormatValue extends AnyValue implements FinalProperty {

		public AnyFormatValue(Object value) {
			super(value, null, ConfigurableEnvironmentProperties.this.getConversionService());
		}

		public String getAsString() {
			return replacePlaceholders(super.getAsString());
		};
	}

	private final DefaultPlaceholderReplacer placeholderReplacer = new DefaultPlaceholderReplacer();
	private ConversionService conversionService;

	@Override
	public void configure(Class<PropertyFactory> serviceClass, ServiceLoaderFactory serviceLoaderFactory) {
		if (!placeholderReplacer.isConfigured()) {
			placeholderReplacer.configure(serviceLoaderFactory);
		}
		super.configure(serviceClass, serviceLoaderFactory);
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public DefaultPlaceholderReplacer getPlaceholderReplacer() {
		return placeholderReplacer;
	}

	public void setParentProperties(EnvironmentProperties parent) {
		if (parent == null) {
			return;
		}

		placeholderReplacer.registerLast(parent.getPlaceholderReplacer());
		registerLast(parent);
	}

	@Override
	public Value get(String key) {
		Value value = super.get(key);
		if (value == null || !value.isPresent()) {
			return Value.EMPTY;
		}

		if (value instanceof FinalProperty) {
			return value;
		}

		return new AnyFormatValue(value);
	}

	public void put(String key, Object value) {
		getMaster().put(key, Value.of(value));
	}
}
