package io.basc.framework.beans.ioc;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.core.reflect.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class NoArgumentMethodIocProcessor extends AbstractIocProcessor {
	private final Method method;

	public NoArgumentMethodIocProcessor(Method method) {
		this.method = method;
		checkMethod(method);
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException {
		if (acceptModifiers(beanDefinition, bean, method.getModifiers())) {
			noArgumentInvoke(bean);
		}
	}

	public Method getMethod() {
		return method;
	}

	public Object noArgumentInvoke(Object bean) throws BeansException {
		return ReflectionUtils.invoke(method, Modifier.isStatic(method.getModifiers()) ? null : bean);
	}
}
