package scw.beans.ioc;

import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public interface IocProcessor {
	Object process(Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	/**
	 * 是否是全局的, 为了兼容老版本支持静态方法
	 * @return
	 */
	boolean isGlobal();
}
