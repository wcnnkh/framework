package io.basc.framework.beans.factory.ioc;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.convert.transform.Property;
import io.basc.framework.core.convert.transform.PropertyDescriptor;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableBeanPropertyResolver extends
		ConfigurableServices<BeanPropertyResolver> implements BeanPropertyResolver {

	public ConfigurableBeanPropertyResolver() {
		setServiceClass(BeanPropertyResolver.class);
	}

	@Override
	public boolean canResolveProperty(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor) {
		return anyMatch((e) -> e.canResolveProperty(beanFactory, propertyDescriptor));
	}

	@Override
	public Property resolveProperty(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor) {
		for (BeanPropertyResolver resolver : this) {
			if (resolver.canResolveProperty(beanFactory, propertyDescriptor)) {
				return resolver.resolveProperty(beanFactory, propertyDescriptor);
			}
		}
		throw new UnsupportedOperationException(propertyDescriptor.toString());
	}
}
