package scw.beans.ioc.value;

import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.ResolvableType;
import scw.io.ResourceUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.event.ObservableResource;
import scw.lang.NotSupportedException;
import scw.mapper.Field;
import scw.util.ConfigUtils;
import scw.value.property.PropertyFactory;

public final class XmlFileToBeanMapValueProcesser extends AbstractResourceValueProcesser<UnsafeByteArrayInputStream> {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName,
			UnsafeByteArrayInputStream inputStream) {
		if (!Map.class.isAssignableFrom(field.getSetter().getType())) {
			throw new NotSupportedException(field.getSetter().toString());
		}

		ResolvableType resolvableType = ResolvableType.forType(field.getSetter().getGenericType());
		return ConfigUtils.xmlToMap(resolvableType.getGeneric(1).getRawClass(), inputStream);
	}

	@Override
	protected ObservableResource<UnsafeByteArrayInputStream> getObservableResource(BeanDefinition beanDefinition,
			BeanFactory beanFactory, PropertyFactory propertyFactory, Object bean, Field field, Value value,
			String name, String charsetName) {
		return ResourceUtils.getResourceOperations().getInputStream(name);
	}
}
