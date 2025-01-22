package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.Value;
import io.basc.framework.util.spi.ConfigurableServices;

public class PropertyPropertyFactories extends ConfigurableServices<PropertyFactory> implements PropertyFactory {

	public PropertyPropertyFactories() {
		setServiceClass(PropertyFactory.class);
	}

	@Override
	public boolean hasProperty(PropertyDescriptor propertyDescriptor) {
		return anyMatch((e) -> e.hasProperty(propertyDescriptor));
	}

	@Override
	public Value getProperty(PropertyDescriptor propertyDescriptor) {
		for (PropertyFactory resolver : this) {
			if (resolver.hasProperty(propertyDescriptor)) {
				return resolver.getProperty(propertyDescriptor);
			}
		}
		throw new UnsupportedOperationException(propertyDescriptor.toString());
	}
}
