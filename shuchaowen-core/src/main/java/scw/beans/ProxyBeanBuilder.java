package scw.beans;

import java.util.Collection;

import scw.aop.FilterChain;
import scw.beans.loader.LoaderContext;
import scw.util.value.property.PropertyFactory;

public class ProxyBeanBuilder extends AutoBeanBuilder {

	public ProxyBeanBuilder(LoaderContext context, Collection<String> proxyNames) {
		this(context, proxyNames, null);
	}

	public ProxyBeanBuilder(LoaderContext context,
			Collection<String> proxyNames, FilterChain filterChain) {
		this(context.getBeanFactory(), context.getPropertyFactory(), context
				.getTargetClass(), proxyNames, filterChain);
	}

	public ProxyBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass,
			Collection<String> proxyNames) {
		this(beanFactory, propertyFactory, targetClass, proxyNames, null);
	}

	public ProxyBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass,
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
