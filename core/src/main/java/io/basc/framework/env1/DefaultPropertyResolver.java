package io.basc.framework.env1;

import java.util.Properties;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.env.properties.EnvironmentProperties;
import io.basc.framework.event.observe.Observable;
import io.basc.framework.text.placeholder.support.HierarchicalPlaceholderReplacer;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.ConfigurationCenter;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class DefaultPropertyResolver extends ConfigurationCenter implements ConfigurablePropertyResolver {

	private static interface FinalProperty {
	}

	private class AnyFormatValue extends AnyValue implements FinalProperty {

		public AnyFormatValue(Value value) {
			super(value.getSource(), value.getTypeDescriptor(), value);
		}

		public String getAsString() {
			return replacePlaceholders(super.getAsString());
		};
	}

	private final HierarchicalPlaceholderReplacer configurablePlaceholderReplacer = new HierarchicalPlaceholderReplacer();
	private EnvironmentProperties parentEnvironmentProperties;
	private Registration parentEnvironmentPropertiesRegistration;

	@Override
	public void configure(Class<PropertyFactory> serviceClass, ServiceLoaderFactory serviceLoaderFactory) {
		if (!configurablePlaceholderReplacer.isConfigured()) {
			configurablePlaceholderReplacer.configure(serviceLoaderFactory);
		}
		super.configure(serviceClass, serviceLoaderFactory);
	}

	@Override
	public HierarchicalPlaceholderReplacer getPlaceholderReplacer() {
		return configurablePlaceholderReplacer;
	}

	public EnvironmentProperties getParentEnvironmentProperties() {
		return parentEnvironmentProperties;
	}

	public synchronized void setParentEnvironmentProperties(EnvironmentProperties parentEnvironmentProperties) {
		if (parentEnvironmentProperties == this.parentEnvironmentProperties) {
			return;
		}

		if (parentEnvironmentPropertiesRegistration != null) {
			parentEnvironmentPropertiesRegistration.unregister();
			parentEnvironmentPropertiesRegistration = null;
		}

		this.parentEnvironmentProperties = parentEnvironmentProperties;
		if (this.parentEnvironmentProperties != null) {
			configurablePlaceholderReplacer
					.setParentPlaceholderReplacer(this.parentEnvironmentProperties.getPlaceholderReplacer());
			this.parentEnvironmentPropertiesRegistration = registerLast(parentEnvironmentProperties);
		}
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

	@Override
	public Registration source(Observable<Properties> properties) {
		return getArchive().registerProperties(properties);
	}
}
