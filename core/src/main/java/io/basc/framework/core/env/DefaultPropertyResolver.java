package io.basc.framework.core.env;

import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.core.convert.lang.ObjectValue;
import io.basc.framework.observe.properties.DynamicPropertyRegistry;
import io.basc.framework.text.placeholder.support.HierarchicalPlaceholderReplacer;
import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.util.Registration;
import io.basc.framework.util.spi.Configurable;

public class DefaultPropertyResolver extends DynamicPropertyRegistry
		implements ConfigurablePropertyResolver, Configurable {

	private static interface FinalProperty {
	}

	private class AnyFormatValue extends ObjectValue implements FinalProperty {
		private static final long serialVersionUID = 1L;

		public AnyFormatValue(ValueWrapper value) {
			super(value.getValue(), value.getTypeDescriptor());
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

			getFactories().setLastService(parentPropertyResolver);
			this.parentPropertyResolver = parentPropertyResolver;
			if (this.parentPropertyResolver != null) {
				configurablePlaceholderReplacer
						.setParentPlaceholderReplacer(this.parentPropertyResolver.getPlaceholderReplacer());
			}
		}
	}

	public ValueWrapper get(String key) {
		ValueWrapper value = super.get(key);
		if (value == null || !value.isPresent()) {
			return ValueWrapper.EMPTY;
		}

		if (value instanceof FinalProperty) {
			return value;
		}

		return new AnyFormatValue(value);
	}

	public Registration register(PropertyFactory propertyFactory) {
		return getFactories().register(propertyFactory);
	}
}
