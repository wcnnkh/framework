package scw.beans.ioc.value;

import java.util.Properties;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.TypeUtils;
import scw.io.ResourceUtils;
import scw.io.event.ObservableResource;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.MapperUtils;
import scw.value.ValueUtils;
import scw.value.property.PropertyFactory;

public final class PropertiesFileValueProcesser extends AbstractResourceValueProcesser<Properties> {

	@Override
	protected ObservableResource<Properties> getObservableResource(BeanDefinition beanDefinition,
			BeanFactory beanFactory, PropertyFactory propertyFactory, Object bean, Field field, Value value,
			String name, String charsetName) {
		return ResourceUtils.getResourceOperations().getProperties(name, charsetName);
	}

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, Properties properties) {
		if (ClassUtils.isPrimitiveOrWrapper(field.getSetter().getType())
				|| TypeUtils.isString(field.getSetter().getType())) {
			return ValueUtils.parse(properties.getProperty(field.getSetter().getName()),
					field.getSetter().getGenericType());
		} else if (Properties.class.isAssignableFrom(field.getSetter().getType())) {
			return properties;
		} else {
			Class<?> fieldType = field.getSetter().getType();
			Object obj = InstanceUtils.INSTANCE_FACTORY.getInstance(fieldType);
			for (final Object key : properties.keySet()) {
				Field keyField = MapperUtils.getMapper().getField(fieldType, new FieldFilter() {

					public boolean accept(Field field) {
						if (field.isSupportSetter()) {
							return field.getSetter().getName().equals(key.toString());
						}
						return false;
					}
				});
				if (keyField == null) {
					continue;
				}

				MapperUtils.setStringValue(keyField, obj, properties.getProperty(keyField.getSetter().getName()));
			}
			return obj;
		}
	}
}