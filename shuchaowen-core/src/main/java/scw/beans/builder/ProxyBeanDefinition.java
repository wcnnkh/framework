package scw.beans.builder;

import scw.aop.Filter;
import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.value.property.PropertyFactory;

public class ProxyBeanDefinition extends DefaultBeanDefinition {

	public ProxyBeanDefinition(LoaderContext context, Filter... filters) {
		this(context.getBeanFactory(), context.getPropertyFactory(), context
				.getTargetClass(), filters);
	}

	public ProxyBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass,
			Filter... filters) {
		super(beanFactory, propertyFactory, targetClass);
		for (Filter filter : filters) {
			super.filters.add(filter);
		}
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
