package scw.aop.support;

import java.util.Arrays;

import scw.aop.Proxy;
import scw.aop.ProxyUtils;

public final class FieldSetterListenUtils {
	private FieldSetterListenUtils() {
	};

	private static final Class<?>[] FIELD_SETTER_LISTEN_INTERFACES = new Class<?>[] { FieldSetterListen.class };

	public static Proxy getFieldSetterListenProxy(Class<?> clazz) {
		return ProxyUtils.getProxyFactory().getProxy(clazz,
				FIELD_SETTER_LISTEN_INTERFACES, Arrays.asList(new FieldSetterListenMethodInterceptor()));
	}

	public static void clearFieldSetterListen(Object instance) {
		if (instance instanceof FieldSetterListen) {
			((FieldSetterListen) instance).clear_field_setter_listen();
		}
	}
}
