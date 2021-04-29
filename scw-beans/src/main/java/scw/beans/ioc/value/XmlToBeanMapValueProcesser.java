package scw.beans.ioc.value;

import java.nio.charset.Charset;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.convert.TypeDescriptor;
import scw.convert.support.CollectionToMapConversionService;
import scw.io.Resource;
import scw.lang.NotSupportedException;
import scw.mapper.Field;

public final class XmlToBeanMapValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value, String name, Charset charset, Resource resource) {
		if (!Map.class.isAssignableFrom(field.getSetter().getType())) {
			throw new NotSupportedException(field.getSetter().toString());
		}
		
		CollectionToMapConversionService service = new CollectionToMapConversionService(beanFactory.getEnvironment(), CollectionToMapConversionService.FIRST_FIELD);
		return service.convert(resource, TypeDescriptor.valueOf(Resource.class), new TypeDescriptor(field.getSetter()));
	}
}
