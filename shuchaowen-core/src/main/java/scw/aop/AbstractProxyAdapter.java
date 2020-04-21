package scw.aop;

import java.util.Collection;

import scw.core.utils.ArrayUtils;

public abstract class AbstractProxyAdapter implements ProxyAdapter {

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces,
			Collection<? extends Filter> filters) {
		return proxy(clazz, interfaces, filters, null);
	}

	protected Class<?>[] getInterfaces(Class<?> clazz, Class<?>[] interfaces) {
		if (ArrayUtils.isEmpty(interfaces) && clazz.isInterface()) {
			return new Class<?>[] { clazz };
		}

		if (clazz.isInterface()) {
			Class<?>[] array = new Class<?>[1 + interfaces.length];
			array[0] = clazz;
			System.arraycopy(interfaces, 0, array, 1, interfaces.length);
			return array;
		}
		return interfaces;
	}
}
