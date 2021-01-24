package scw.beans.ioc.value;

import java.nio.charset.Charset;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.convert.Converter;
import scw.event.Observable;
import scw.io.Resource;
import scw.io.event.ObservableResourceUtils;
import scw.mapper.Field;

public abstract class AbstractObservableResourceValueProcesser extends AbstractObservableValueProcesser<Resource> {

	@Override
	protected Observable<Resource> getObservableResource(BeanDefinition beanDefinition, BeanFactory beanFactory, Object bean, Field field, Value value, String name, Charset charset) {
		final Resource resource = beanFactory.getEnvironment().getResource(name);
		return ObservableResourceUtils.getObservableResource(resource, new Converter<Resource, Resource>() {

			public Resource convert(Resource k) {
				return k;
			}
		});
	}
}
