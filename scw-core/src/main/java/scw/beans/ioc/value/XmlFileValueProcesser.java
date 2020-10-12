package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.io.Resource;
import scw.json.JSONUtils;
import scw.mapper.Field;
import scw.util.ConfigUtils;
import scw.value.property.PropertyFactory;

public class XmlFileValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, Resource resource)
			throws Exception {
		return ConfigUtils.parse(field.getSetter().getGenericType(), resource, JSONUtils.getJsonSupport());
	}
}
