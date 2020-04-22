package scw.beans;

import java.util.Collection;

import scw.util.value.property.PropertyFactory;

public class ProxyBeanBuilder extends AutoBeanBuilder {

	public ProxyBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Collection<String> proxyNames) {
		super(beanFactory, propertyFactory, targetClass);
		filterNames.addAll(proxyNames);
	}

	@Override
	public boolean isInstance() {
		return true;
	}

	@Override
	public Object create() throws Exception {
		if (getTargetClass().isInterface()) {
			return createProxyInstance(getTargetClass(), null, null);
		}

		return super.create();
	}
}
