package scw.beans.builder;

import java.util.Collection;

import scw.aop.Filter;
import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.core.instance.InstanceIterable;
import scw.value.property.PropertyFactory;

public class ProxyBeanDefinition extends DefaultBeanDefinition {
	private Iterable<? extends Filter> filters;

	public ProxyBeanDefinition(LoaderContext context, Collection<String> filterNames) {
		this(context, new InstanceIterable<Filter>(context.getBeanFactory(), filterNames));
	}

	public ProxyBeanDefinition(LoaderContext context, Iterable<? extends Filter> filters) {
		this(context.getBeanFactory(), context.getPropertyFactory(), context.getTargetClass(), filters);
	}

	public ProxyBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Collection<String> filterNames) {
		this(beanFactory, propertyFactory, targetClass, new InstanceIterable<Filter>(beanFactory, filterNames));
	}

	public ProxyBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Iterable<? extends Filter> filters) {
		super(beanFactory, propertyFactory, targetClass);
		this.filters = filters;
	}

	@Override
	public Iterable<? extends Filter> getFilters() {
		return filters;
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
