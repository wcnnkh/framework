package io.basc.framework.context.ioc.support;

import java.nio.charset.Charset;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.io.Resource;
import io.basc.framework.io.event.ObservableResourceUtils;
import io.basc.framework.mapper.Field;

public abstract class AbstractObservableResourceValueProcessor extends AbstractObservableValueProcessor<Resource> {

	@Override
	protected Observable<Resource> getObservableResource(BeanDefinition beanDefinition, Context context, Object bean,
			Field field, ValueDefinition valueDefinition, String name, Charset charset) {
		final Resource resource = context.getResourceLoader().getResource(name);
		if (valueDefinition.isRequired() && (resource == null || !resource.exists())) {
			return null;
		}
		return ObservableResourceUtils.getResource(resource);
	}
}
