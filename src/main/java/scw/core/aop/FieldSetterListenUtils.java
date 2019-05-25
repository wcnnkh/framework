package scw.core.aop;

import java.io.Serializable;

import net.sf.cglib.proxy.Enhancer;

public final class FieldSetterListenUtils {
	private static final Class<?>[] FIELD_SETTER_LISTEN_INTERFACES = new Class<?>[] { FieldSetterListen.class };

	@SuppressWarnings("unchecked")
	public static Class<? extends FieldSetterListen> createFieldSetterListenProxyClass(
			Class<?> clazz,
			Class<? extends FieldSetterListenInterceptor> interceptorClass) {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(FIELD_SETTER_LISTEN_INTERFACES);
		enhancer.setCallbackType(interceptorClass);
		enhancer.setSuperclass(clazz);
		if (Serializable.class.isAssignableFrom(clazz)) {
			enhancer.setSerialVersionUID(1L);
		}
		return enhancer.createClass();
	}

	public static <T extends FieldSetterListenInterceptor> Enhancer getFieldSetterListenEnhacer(
			Class<?> clazz, FieldSetterListenInterceptor interceptor) {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(FIELD_SETTER_LISTEN_INTERFACES);
		enhancer.setCallback(interceptor);
		enhancer.setSuperclass(clazz);
		if (Serializable.class.isAssignableFrom(clazz)) {
			enhancer.setSerialVersionUID(1L);
		}
		return enhancer;
	}

	public static Class<? extends FieldSetterListen> createFieldSetterListenProxyClass(
			Class<?> clazz) {
		return createFieldSetterListenProxyClass(clazz,
				FieldSetterListenInterceptor.class);
	}

	public static Object newFieldSetterListenInstance(Class<?> clazz) {
		return getFieldSetterListenEnhacer(clazz,
				new FieldSetterListenInterceptor()).create();
	}
}
