package io.basc.framework.beans.ioc.value;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.Resource;
import io.basc.framework.mapper.Field;

import java.nio.charset.Charset;

/**
 * 资源解析
 * @author shuchaowen
 *
 */
public class ResourceValueProcessor extends AbstractObservableResourceValueProcessor {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value, String name, Charset charset, Resource resource)
			throws Exception {
		return beanFactory.getEnvironment().getResourceResolver().resolveResource(resource, new TypeDescriptor(field.getSetter()));
	}
}
