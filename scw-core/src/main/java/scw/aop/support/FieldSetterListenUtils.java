package scw.aop.support;

import scw.aop.MethodInterceptor;
import scw.aop.Proxy;
import scw.aop.ProxyFactory;

public final class FieldSetterListenUtils {
	private FieldSetterListenUtils() {
	};

	private static final Class<?>[] FIELD_SETTER_LISTEN_INTERFACES = new Class<?>[] { FieldSetterListen.class };

	public static Proxy getFieldSetterListenProxy(ProxyFactory proxyFactory, Class<?> clazz) {
		MethodInterceptor methodInterceptor = new FieldSetterListenMethodInterceptor();
		return proxyFactory.getProxy(clazz,
				FIELD_SETTER_LISTEN_INTERFACES, methodInterceptor);
	}

	public static void clearFieldSetterListen(Object instance) {
		if (instance instanceof FieldSetterListen) {
			((FieldSetterListen) instance).clear_field_setter_listen();
		}
	}
}
