package io.basc.framework.beans.factory.di;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.mapping.Property;
import io.basc.framework.core.mapping.PropertyDescriptor;

/**
 * 依赖注入属性解析
 * 
 * @author shuchaowen
 *
 */
public interface BeanPropertyResolver {
	boolean canResolveProperty(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor);

	Property resolveProperty(BeanFactory beanFactory, PropertyDescriptor propertyDescriptor);
}
