package scw.aop.support;

import java.io.Serializable;

import scw.aop.Proxy;
import scw.core.cglib.proxy.Enhancer;
import scw.core.cglib.proxy.MethodInterceptor;

public class BuiltInCglibProxy implements Proxy {
	private Enhancer enhancer;

	public BuiltInCglibProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		this.enhancer = createEnhancer(clazz, interfaces);
		this.enhancer.setCallback(methodInterceptor);
	}

	public Object create() {
		return enhancer.create();
	}

	public Object create(Class<?>[] parameterTypes, Object[] arguments) {
		return enhancer.create(parameterTypes, arguments);
	}

	public static Enhancer createEnhancer(Class<?> clazz, Class<?>[] interfaces) {
		Enhancer enhancer = new Enhancer();
		if (Serializable.class.isAssignableFrom(clazz)) {
			enhancer.setSerialVersionUID(1L);
		}
		if (interfaces != null) {
			enhancer.setInterfaces(interfaces);
		}
		enhancer.setSuperclass(clazz);
		return enhancer;
	}
}
