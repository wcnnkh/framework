package scw.beans.ioc.value;

import java.util.Collection;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.configure.support.ConfigureUtils;
import scw.core.ResolvableType;
import scw.io.Resource;
import scw.lang.NotSupportedException;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public final class XmlFileToListMapValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, Resource resource)
			throws Exception {
		if (!Collection.class.isAssignableFrom(field.getSetter().getType())) {
			throw new NotSupportedException(field.getSetter().toString());
		}

		if (!Map.class.isAssignableFrom(
				ResolvableType.forType(field.getSetter().getGenericType()).getGeneric(0).getRawClass())) {
			throw new NotSupportedException(field.getSetter().toString());
		}

		return ConfigureUtils.getDefaultXmlContent(resource, "config");
	}
}
