package io.basc.framework.beans.ioc.value;

import java.nio.charset.Charset;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.event.Observable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.event.ObservableResourceUtils;
import io.basc.framework.mapper.Field;

public abstract class AbstractObservableResourceValueProcessor extends AbstractObservableValueProcessor<Resource> {

	@Override
	protected Observable<Resource> getObservableResource(BeanDefinition beanDefinition, BeanFactory beanFactory, Object bean, Field field, Value value, String name, Charset charset) {
		final Resource resource = beanFactory.getEnvironment().getResource(name);
		return ObservableResourceUtils.getResource(resource);
	}
}
