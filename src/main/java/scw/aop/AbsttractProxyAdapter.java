package scw.aop;

import java.util.Collection;

import scw.core.utils.ArrayUtils;

public abstract class AbsttractProxyAdapter implements ProxyAdapter {

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters) {
		return proxy(clazz, interfaces, filters, null);
	}

	protected Class<?>[] getInterfaces(Class<?> clazz, Class<?>[] interfaces) {
		if (ArrayUtils.isEmpty(interfaces)) {
			return clazz.isInterface() ? new Class<?>[] { clazz } : clazz.getInterfaces();
		}
		return interfaces;
	}
}
