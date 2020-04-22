package scw.aop;

import scw.core.utils.ArrayUtils;

public abstract class AbstractProxyAdapter implements ProxyAdapter {

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
