package io.basc.framework.env1;

import java.util.Properties;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
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
	private PropertyResolver parentPropertyResolver;
	private Registration parentPropertyResolverRegistration;

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

	public PropertyResolver getParentPropertyResolver() {
		synchronized (this) {
			return parentPropertyResolver;
		}
	}

	public synchronized void setParentPropertyResolver(PropertyResolver parentPropertyResolver) {
		synchronized (this) {
			if (parentPropertyResolver == this.parentPropertyResolver) {
				return;
			}

			if (parentPropertyResolverRegistration != null) {
				parentPropertyResolverRegistration.unregister();
				parentPropertyResolverRegistration = null;
			}

			this.parentPropertyResolver = parentPropertyResolver;
			if (this.parentPropertyResolver != null) {
				configurablePlaceholderReplacer
						.setParentPlaceholderReplacer(this.parentPropertyResolver.getPlaceholderReplacer());
				this.parentPropertyResolverRegistration = registerLast(parentPropertyResolver);
			}
		}
	}

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
