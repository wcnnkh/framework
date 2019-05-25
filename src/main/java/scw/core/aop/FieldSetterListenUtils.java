package scw.core.aop;

import java.io.Serializable;

import net.sf.cglib.proxy.Enhancer;

public final class FieldSetterListenUtils {

	public static Class<?>[] getFieldSetterListenProxyInterfaces(Class<?> clazz) {
		Class<?>[] interfaces;
		if (FieldSetterListen.class.isAssignableFrom(clazz)) {
			interfaces = clazz.getInterfaces();
		} else {// 没有自己实现此接口，增加此接口
			Class<?>[] arr = clazz.getInterfaces();
			if (arr.length == 0) {
				interfaces = new Class[] { FieldSetterListen.class };
			} else {
				interfaces = new Class[arr.length + 1];
				System.arraycopy(arr, 0, interfaces, 0, arr.length);
				interfaces[arr.length] = FieldSetterListen.class;
			}
		}
		return interfaces;
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends FieldSetterListen> createFieldSetterListenProxyClass(
			Class<?> clazz,
			Class<? extends FieldSetterListenInterceptor> interceptorClass) {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(getFieldSetterListenProxyInterfaces(clazz));
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
		enhancer.setInterfaces(getFieldSetterListenProxyInterfaces(clazz));
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
