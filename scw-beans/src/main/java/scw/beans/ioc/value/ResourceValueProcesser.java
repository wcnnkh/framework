package scw.beans.ioc.value;

import java.nio.charset.Charset;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.convert.TypeDescriptor;
import scw.io.Resource;
import scw.mapper.Field;

/**
 * 资源解析
 * @author shuchaowen
 *
 */
public class ResourceValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value, String name, Charset charset, Resource resource)
			throws Exception {
		return beanFactory.getEnvironment().getResourceResolver().resolveResource(resource, new TypeDescriptor(field.getSetter()));
	}
}
