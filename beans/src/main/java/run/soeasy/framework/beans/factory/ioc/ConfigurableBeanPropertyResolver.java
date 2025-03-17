package run.soeasy.framework.beans.factory.ioc;

import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.core.convert.transform.stereotype.Property;
import run.soeasy.framework.core.convert.transform.stereotype.PropertyDescriptor;
import run.soeasy.framework.util.spi.ConfigurableServices;

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
