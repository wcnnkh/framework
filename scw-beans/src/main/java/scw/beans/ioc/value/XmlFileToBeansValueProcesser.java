package scw.beans.ioc.value;

import java.util.Collection;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.configure.support.ConfigureUtils;
import scw.core.ResolvableType;
import scw.io.Resource;
import scw.lang.NotSupportedException;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

/**
 * xml解析
 * 
 * @author shuchaowen
 *
 */
public final class XmlFileToBeansValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, Resource resource) {
		if (!Collection.class.isAssignableFrom(field.getSetter().getType())) {
			throw new NotSupportedException(field.getSetter().toString());
		}

		ResolvableType resolvableType = ResolvableType.forType(field.getSetter().getGenericType());
		return ConfigureUtils.xmlToList(resolvableType.getGeneric(0).getRawClass(), resource);
	}
}
