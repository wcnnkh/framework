package io.basc.framework.context.ioc.support;

import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.mapper.support.DefaultObjectMapping;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.value.Value;

public final class PropertiesFileValueProcessor extends AbstractObservableValueProcessor<Properties> {

	@Override
	protected Observable<Properties> getObservableResource(BeanDefinition beanDefinition, Context context, Object bean,
			Field field, ValueDefinition valueDefinition, String name, Charset charset) {
		return context.getProperties(name, charset);
	}

	@Override
	protected Object parse(BeanDefinition beanDefinition, Context context, Object bean, Field field,
			ValueDefinition value, String name, Charset charset, Properties properties) {
		if (ClassUtils.isPrimitiveOrWrapper(field.getSetter().getType())
				|| field.getSetter().getType() == String.class) {
			return Value.of(properties.getProperty(field.getSetter().getName()))
					.getAsObject(field.getSetter().getGenericType());
		} else if (Properties.class.isAssignableFrom(field.getSetter().getType())) {
			return properties;
		} else {
			Class<?> fieldType = field.getSetter().getType();
			Object obj = ReflectionApi.newInstance(fieldType);
			DefaultObjectMapping fields = DefaultObjectMapping.getFields(fieldType).all().filter(FieldFeature.SUPPORT_SETTER).shared();
			for (final Object key : properties.keySet()) {
				Field keyField = fields.getBySetterName(key.toString(), null);
				if (keyField == null) {
					continue;
				}

				MapperUtils.setValue(context.getConversionService(), obj, keyField,
						properties.getProperty(keyField.getSetter().getName()));
			}
			return obj;
		}
	}
}