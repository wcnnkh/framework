package io.basc.framework.beans.ioc.value;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.env.Sys;
import io.basc.framework.event.Observable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.value.StringValue;

import java.nio.charset.Charset;
import java.util.Properties;

public final class PropertiesFileValueProcessor extends AbstractObservableValueProcessor<Properties> {

	@Override
	protected Observable<Properties> getObservableResource(BeanDefinition beanDefinition,
			BeanFactory beanFactory, Object bean, Field field, Value value,
			String name, Charset charset) {
		return beanFactory.getEnvironment().getProperties(name, charset);
	}

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value, String name, Charset charset, Properties properties) {
		if (ClassUtils.isPrimitiveOrWrapper(field.getSetter().getType())
				|| field.getSetter().getType() == String.class) {
			return StringValue.parse(properties.getProperty(field.getSetter().getName()),
					field.getSetter().getGenericType());
		} else if (Properties.class.isAssignableFrom(field.getSetter().getType())) {
			return properties;
		} else {
			Class<?> fieldType = field.getSetter().getType();
			Object obj = Sys.env.getInstance(fieldType);
			Fields fields = MapperUtils.getFields(fieldType).all().accept(FieldFeature.SUPPORT_SETTER);
			for (final Object key : properties.keySet()) {
				Field keyField = fields.findSetter(key.toString(), null);
				if (keyField == null) {
					continue;
				}
				
				MapperUtils.setValue(beanFactory.getEnvironment().getConversionService(), obj, keyField, properties.getProperty(keyField.getSetter().getName()));
			}
			return obj;
		}
	}
}