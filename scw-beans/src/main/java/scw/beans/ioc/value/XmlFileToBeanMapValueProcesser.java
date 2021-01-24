package scw.beans.ioc.value;

import java.nio.charset.Charset;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.configure.convert.CollectionToMapConversionService;
import scw.configure.convert.PrimaryKeyGetter;
import scw.configure.support.ConfigureUtils;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.lang.NotSupportedException;
import scw.mapper.Field;

public final class XmlFileToBeanMapValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value, String name, Charset charset, Resource resource) {
		if (!Map.class.isAssignableFrom(field.getSetter().getType())) {
			throw new NotSupportedException(field.getSetter().toString());
		}
		
		CollectionToMapConversionService service = new CollectionToMapConversionService(ConfigureUtils.getConversionServiceFactory(), PrimaryKeyGetter.FIRST_FIELD);
		return service.convert(resource, TypeDescriptor.valueOf(Resource.class), new TypeDescriptor(field.getSetter()));
	}
}
