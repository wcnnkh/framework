package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.convert.Converter;
import scw.event.Observable;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.io.event.ObservableResourceUtils;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public abstract class AbstractObservableResourceValueProcesser extends AbstractObservableValueProcesser<Resource> {

	@Override
	protected Observable<Resource> getObservableResource(BeanDefinition beanDefinition, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Object bean, Field field, Value value, String name, String charsetName) {
		final Resource resource = ResourceUtils.getResourceOperations().getResource(name);
		return ObservableResourceUtils.getObservableResource(resource, new Converter<Resource, Resource>() {

			public Resource convert(Resource k) {
				return k;
			}
		});
	}
}
