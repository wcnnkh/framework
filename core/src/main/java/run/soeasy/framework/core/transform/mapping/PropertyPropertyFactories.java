package run.soeasy.framework.core.transform.mapping;

import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class PropertyPropertyFactories extends ConfigurableServices<PropertyFactory> implements PropertyFactory {

	public PropertyPropertyFactories() {
		setServiceClass(PropertyFactory.class);
	}

	@Override
	public boolean hasProperty(PropertyDescriptor propertyDescriptor) {
		return anyMatch((e) -> e.hasProperty(propertyDescriptor));
	}

	@Override
	public Source getProperty(PropertyDescriptor propertyDescriptor) {
		for (PropertyFactory resolver : this) {
			if (resolver.hasProperty(propertyDescriptor)) {
				return resolver.getProperty(propertyDescriptor);
			}
		}
		throw new UnsupportedOperationException(propertyDescriptor.toString());
	}
}
