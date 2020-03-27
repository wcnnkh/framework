package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.util.value.property.PropertyFactory;

public class NoArgumentBeanMethod implements BeanMethod {
	private final Method method;
	private final boolean isStaticMethod;

	public NoArgumentBeanMethod(Method method) {
		this.method = method;
		this.isStaticMethod = Modifier.isStatic(method.getModifiers());
	}

	public Object invoke(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		return noArgumentInvoke(bean);
	}

	public Method getMethod() {
		return method;
	}
	
	public Object invoke(Object bean) throws Exception{
		return noArgumentInvoke(bean);
	}

	public Object noArgumentInvoke(Object bean) throws Exception {
		return method.invoke(isStaticMethod ? null : bean);
	}
}
