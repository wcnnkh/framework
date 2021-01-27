package scw.beans.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.Collection;

import scw.aop.MethodInterceptor;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.instance.support.InstanceIterable;

public class ProxyBeanDefinition extends DefaultBeanDefinition {
	private Iterable<? extends MethodInterceptor> filters;

	public ProxyBeanDefinition(BeanFactory beanFactory, Class<?> targetClass,
			Collection<String> filterNames) {
		this(beanFactory,  targetClass, new InstanceIterable<MethodInterceptor>(beanFactory, filterNames));
	}

	public ProxyBeanDefinition(BeanFactory beanFactory, Class<?> targetClass,
			Iterable<? extends MethodInterceptor> filters) {
		super(beanFactory, targetClass);
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
	public Object create() throws BeansException {
		if (getTargetClass().isInterface()) {
			return createProxyInstance(getTargetClass(), null, null);
		}
		return super.create();
	}
}
