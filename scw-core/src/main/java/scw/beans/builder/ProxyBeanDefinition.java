package scw.beans.builder;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.Collection;

import scw.aop.MethodInterceptor;
import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.core.instance.InstanceIterable;
import scw.value.property.PropertyFactory;

public class ProxyBeanDefinition extends DefaultBeanDefinition {
	private Iterable<? extends MethodInterceptor> filters;

	public ProxyBeanDefinition(LoaderContext context, Collection<String> filterNames) {
		this(context, new InstanceIterable<MethodInterceptor>(context.getBeanFactory(), filterNames));
	}

	public ProxyBeanDefinition(LoaderContext context, Iterable<? extends MethodInterceptor> filters) {
		this(context.getBeanFactory(), context.getPropertyFactory(), context.getTargetClass(), filters);
	}

	public ProxyBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Collection<String> filterNames) {
		this(beanFactory, propertyFactory, targetClass, new InstanceIterable<MethodInterceptor>(beanFactory, filterNames));
	}

	public ProxyBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Iterable<? extends MethodInterceptor> filters) {
		super(beanFactory, propertyFactory, targetClass);
		this.filters = filters;
	}

	@Override
	public Iterable<? extends MethodInterceptor> getFilters() {
		return filters;
	}
	
	@Override
	public boolean isAopEnable(Class<?> clazz, AnnotatedElement annotatedElement) {
		return Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface() || super.isAopEnable(clazz, annotatedElement);
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
