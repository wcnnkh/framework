package scw.beans.ioc.value;

import java.util.Properties;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.configure.support.ConfigureUtils;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.TypeUtils;
import scw.event.Observable;
import scw.io.ResourceUtils;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.value.ValueUtils;
import scw.value.property.PropertyFactory;

public final class PropertiesFileValueProcesser extends AbstractObservableValueProcesser<Properties> {

	@Override
	protected Observable<Properties> getObservableResource(BeanDefinition beanDefinition,
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
			Fields fields = MapperUtils.getMapper().getFields(fieldType, FilterFeature.SUPPORT_SETTER);
			for (final Object key : properties.keySet()) {
				Field keyField = fields.findSetter(key.toString(), null);
				if (keyField == null) {
					continue;
				}

				ConfigureUtils.setValue(obj, keyField, properties.getProperty(keyField.getSetter().getName()));
			}
			return obj;
		}
	}
}