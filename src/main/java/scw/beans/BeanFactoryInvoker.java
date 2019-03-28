package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.aop.Invoker;

public final class BeanFactoryInvoker implements Invoker{
	private final BeanFactory beanFactory;
	private final Class<?> clz;
	private final Method method;

	public BeanFactoryInvoker(BeanFactory beanFactory, Class<?> clz, Method method) {
		this.clz = clz;
		this.method = method;
		this.beanFactory = beanFactory;
	}

	public Object invoke(Object... args) throws Throwable {
		if (Modifier.isStatic(method.getModifiers())) {
			return method.invoke(null, args);
		} else {
			return method.invoke(beanFactory.get(clz), args);
		}
	}
}
