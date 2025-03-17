package run.soeasy.framework.beans.factory.ioc;

import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.core.convert.transform.stereotype.Property;
import run.soeasy.framework.core.convert.transform.stereotype.PropertyDescriptor;

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
