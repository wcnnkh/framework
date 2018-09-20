package shuchaowen.core.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import shuchaowen.core.beans.BeanFactory;

public class ReflectInvoker implements Invoker {
	private Object bean;// 如果是静态方法这个就应该是空
	private Method method;
	
	public ReflectInvoker(BeanFactory beanFactory, Class<?> type, Method method) {
		this.bean = Modifier.isStatic(method.getModifiers())? null:beanFactory.get(type);
		method.setAccessible(true);
		this.method = method;
	}

	public ReflectInvoker(Object bean, Method method) {
		this.bean = Modifier.isStatic(method.getModifiers())? null:bean;
		method.setAccessible(true);
		this.method = method;
	}

	public Object invoke(Object... args) throws Throwable {
		return method.invoke(bean, args);
	}
}
