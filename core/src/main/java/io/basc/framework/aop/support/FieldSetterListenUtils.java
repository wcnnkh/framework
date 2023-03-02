package io.basc.framework.aop.support;

import java.util.Collections;
import java.util.Map;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.Proxy;
import io.basc.framework.aop.ProxyFactory;

public final class FieldSetterListenUtils {
	private FieldSetterListenUtils() {
	};

	private static final Class<?>[] FIELD_SETTER_LISTEN_INTERFACES = new Class<?>[] { FieldSetterListen.class };

	public static Proxy getProxy(ProxyFactory proxyFactory, Class<?> clazz) {
		MethodInterceptor methodInterceptor = new FieldSetterListenMethodInterceptor();
		return proxyFactory.getProxy(clazz, FIELD_SETTER_LISTEN_INTERFACES, methodInterceptor);
	}

	public static void clearFieldSetterListen(Object instance) {
		if (instance instanceof FieldSetterListen) {
			((FieldSetterListen) instance)._clearFieldSetterMap();
		}
	}

	public static Proxy getProxy(Class<?> type) {
		return getProxy(ProxyUtils.getFactory(), type);
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type) {
		return (T) getProxy(type).create();
	}

	/**
	 * @param instance 实例
	 * @return 返回空说明这不是一个可以监听的实例
	 */
	public static Map<String, Object> getChangeMap(Object instance) {
		if (instance instanceof FieldSetterListen) {
			Map<String, Object> map = ((FieldSetterListen) instance)._getFieldSetterMap();
			return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
		}
		return null;
	}
}
