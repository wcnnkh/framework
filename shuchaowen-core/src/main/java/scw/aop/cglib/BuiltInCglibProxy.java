package scw.aop.cglib;

import java.io.Serializable;

import scw.aop.AbstractProxy;
import scw.cglib.proxy.Enhancer;
import scw.cglib.proxy.MethodInterceptor;

public class BuiltInCglibProxy extends AbstractProxy {
	private Enhancer enhancer;

	public BuiltInCglibProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		super(clazz);
		this.enhancer = createEnhancer(clazz, interfaces);
		this.enhancer.setCallback(methodInterceptor);
	}

	public Object create() {
		return enhancer.create();
	}

	public Object createInternal(Class<?>[] parameterTypes, Object[] arguments) {
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
