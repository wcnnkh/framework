package scw.beans;

import java.util.Collection;

import scw.aop.FilterChain;
import scw.util.value.property.PropertyFactory;

public class ProxyBeanBuilder extends AutoBeanBuilder {

	public ProxyBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Collection<String> proxyNames) {
		this(beanFactory, propertyFactory, targetClass, proxyNames, null);
	}

	public ProxyBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Collection<String> proxyNames, FilterChain filterChain) {
		super(beanFactory, propertyFactory, targetClass);
		if (proxyNames != null) {
			filterNames.addAll(proxyNames);
		}
		this.filterChain = filterChain;
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
