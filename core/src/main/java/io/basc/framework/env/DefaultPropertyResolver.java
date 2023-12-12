package io.basc.framework.env;

import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.observe.properties.DynamicPropertyFactory;
import io.basc.framework.text.placeholder.support.HierarchicalPlaceholderReplacer;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

public class DefaultPropertyResolver extends DynamicPropertyFactory
		implements ConfigurablePropertyResolver, Configurable {

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

	private AtomicBoolean configured = new AtomicBoolean();

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		if (!configured.compareAndSet(false, true)) {
			return;
		}

		if (!configurablePlaceholderReplacer.isConfigured()) {
			configurablePlaceholderReplacer.configure(serviceLoaderFactory);
		}

		if (!getFactories().isConfigured()) {
			getFactories().configure(serviceLoaderFactory);
		}
	}

	@Override
	public boolean isConfigured() {
		return configured.get();
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

			this.parentPropertyResolver = parentPropertyResolver;
			if (this.parentPropertyResolver != null) {
				configurablePlaceholderReplacer
						.setParentPlaceholderReplacer(this.parentPropertyResolver.getPlaceholderReplacer());
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
}
