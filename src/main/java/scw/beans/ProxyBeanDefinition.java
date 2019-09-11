package scw.beans;

import java.util.Arrays;

import scw.beans.annotation.Proxy;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.aop.Filter;
import scw.core.aop.FilterInvocationHandler;

/**
 * 使用jdk代理以提高性能
 * @author shuchaowen
 *
 */
public class ProxyBeanDefinition extends CommonBeanDefinition {
	public ProxyBeanDefinition(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> type, String[] filterNames) {
		super(valueWiredManager, beanFactory, propertyFactory, type, filterNames);
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		if (getType().isInterface()) {
			Proxy proxy = getType().getAnnotation(Proxy.class);
			Filter filter = beanFactory.getInstance(proxy.value());
			Object newProxyInstance = java.lang.reflect.Proxy.newProxyInstance(getType().getClassLoader(),
					new Class[] { getType() }, new FilterInvocationHandler(Arrays.asList(filter)));
			return (T) BeanUtils.proxyInterface(beanFactory, getType(), newProxyInstance, filterNames);
		}
		return super.create();
	}
}
