package scw.beans.ioc.value;

import java.nio.charset.Charset;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.configure.support.ConfigureUtils;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.mapper.Field;

public class ResourceValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value, String name, Charset charset, Resource resource)
			throws Exception {
		return ConfigureUtils.getConversionServiceFactory().convert(resource, new TypeDescriptor(field.getSetter()));
	}
}
