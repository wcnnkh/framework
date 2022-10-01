package io.basc.framework.context.ioc.support;

import java.nio.charset.Charset;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.io.Resource;
import io.basc.framework.mapper.Field;

/**
 * 资源解析
 * 
 * @author shuchaowen
 *
 */
public class ResourceValueProcessor extends AbstractObservableResourceValueProcessor {

	@Override
	protected Object parse(BeanDefinition beanDefinition, Context context, Object bean, Field field,
			ValueDefinition value, String name, Charset charset, Resource resource) throws Exception {
		return context.getResourceResolver().resolveResource(resource, new TypeDescriptor(field.getSetter()));
	}
}
