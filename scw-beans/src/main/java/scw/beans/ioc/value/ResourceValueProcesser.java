package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.configure.support.ConfigureUtils;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public class ResourceValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, Resource resource)
			throws Exception {
		return ConfigureUtils.getConversionServiceFactory().convert(resource, new TypeDescriptor(field.getSetter()));
	}
}
