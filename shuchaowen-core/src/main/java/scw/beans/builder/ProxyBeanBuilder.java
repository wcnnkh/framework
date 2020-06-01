package scw.beans.builder;

import scw.aop.Filter;
import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public class ProxyBeanBuilder extends AutoBeanBuilder {

	public ProxyBeanBuilder(LoaderContext context, Filter... filters) {
		this(context.getBeanFactory(), context.getPropertyFactory(), context
				.getTargetClass(), filters);
	}

	public ProxyBeanBuilder(BeanFactory beanFactory,
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
