package io.basc.framework.aop.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.Proxy;
import io.basc.framework.aop.ProxyFactory;

public final class FieldSetterListenUtils {
	private FieldSetterListenUtils() {
	};

	private static final Class<?>[] FIELD_SETTER_LISTEN_INTERFACES = new Class<?>[] { FieldSetterListen.class };

	public static Proxy getFieldSetterListenProxy(ProxyFactory proxyFactory, Class<?> clazz) {
		MethodInterceptor methodInterceptor = new FieldSetterListenMethodInterceptor();
		return proxyFactory.getProxy(clazz, FIELD_SETTER_LISTEN_INTERFACES, methodInterceptor);
	}

	public static void clearFieldSetterListen(Object instance) {
		if (instance instanceof FieldSetterListen) {
			((FieldSetterListen) instance)._clearFieldSetterMap();
		}
	}
}
